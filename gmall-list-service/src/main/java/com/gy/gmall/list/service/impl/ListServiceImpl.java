package com.gy.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.gmall.bean.SkuInfo;
import com.gy.gmall.bean.SkuLsInfo;
import com.gy.gmall.bean.SkuLsParams;
import com.gy.gmall.bean.SkuLsResult;
import com.gy.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListServiceImpl implements ListService {
    @Autowired
    JestClient jestClient;

    public static final String index_name = "gmall";

    public static final String type_name = "SkuInfo";

    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {
        Index index = new Index.Builder(skuLsInfo).
                index(index_name).type(type_name).id(skuLsInfo.getId()).build();
        try {
            DocumentResult execute = jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //查询结果
    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        //构造dsl语句
        String query = makeQueryStringForSearch(skuLsParams);
        Search search = new Search.Builder(query).addIndex(index_name).addType(type_name).build();
        SearchResult searchResult = null;

        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SkuLsResult skuLsResult = makeResultStringForSearch(skuLsParams,searchResult);

        return skuLsResult;
    }

    // 结果集转换
    private SkuLsResult makeResultStringForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        SkuLsResult skuLsResult = new SkuLsResult();
        List<SkuLsInfo> skuLsInfoList = new ArrayList<SkuLsInfo>(skuLsParams.getPageSize());

        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {

            SkuLsInfo skuLsInfo = hit.source;
            if (hit.highlight != null && hit.highlight.size() > 0) {
                List<String> skuName = hit.highlight.get("skuName");
                //将高亮标签的字符串替换成skuname
                String skuNameHighLight = skuName.get(0);
                skuLsInfo.setSkuName(skuNameHighLight);

            }
            skuLsInfoList.add(skuLsInfo);
        }
        skuLsResult.setSkuLsInfoList(skuLsInfoList);
        skuLsResult.setTotal(searchResult.getTotal());
        //计算总页数
        long totalPage = (searchResult.getTotal() + skuLsParams.getPageSize() - 1) / skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPage);

        //取出属性值
        List<String> baseAttrValueIdList = new ArrayList<String>();
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        if (groupby_attr != null) {
            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                baseAttrValueIdList.add(bucket.getKey());
                skuLsResult.setAttrValueIdList(baseAttrValueIdList);
            }
        }

        return skuLsResult;
    }



    //封装查询DSL语句方法
    public String makeQueryStringForSearch(SkuLsParams skuLsParams){
        //构造工具类
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //创建query,bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //创建filter,must
        //keyword相当于传入的关键字
        if(skuLsParams.getKeyword()!=null&&skuLsParams.getKeyword().length()>0){
            //must所对应的对象
            MatchQueryBuilder matchQueryBuilder= new MatchQueryBuilder("skuName",skuLsParams.getKeyword());
            //把must添加到bool中
            boolQueryBuilder.must(matchQueryBuilder);
            //bool和高亮同级,创建高亮对象
            HighlightBuilder highlight = searchSourceBuilder.highlighter();
            highlight.field("skuName");
            highlight.preTags("<span style='color:red'>");
            highlight.postTags("</span>");
            //设置高亮
            searchSourceBuilder.highlight(highlight);

        }
        //设置三级id
        if(skuLsParams.getCatalog3Id()!=null&&skuLsParams.getCatalog3Id().length()>0){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        //设置平台属性值
        if(skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
                String baseAttrValueId = skuLsParams.getValueId()[i];
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", baseAttrValueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        //设置分页
        int from = skuLsParams.getPageSize()*(skuLsParams.getPageNo()-1);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(skuLsParams.getPageSize());
        //排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        //聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);
        //将Must保存
        searchSourceBuilder.query(boolQueryBuilder);
        String query = searchSourceBuilder.toString();
        return query;
    }
}
