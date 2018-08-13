package com.gy.gmall.list.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.gmall.bean.*;
import com.gy.gmall.service.ListService;
import com.gy.gmall.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {
    @Reference
    ListService listService;
    @Reference
    ManagerService managerService;
    @RequestMapping("/list.html")
    public String getList(SkuLsParams skuLsParams,Model model){
        // 设置每页显示的条数
        skuLsParams.setPageSize(2);

// skuLsResult.setTotalPages(totalPages);

        SkuLsResult skuLsResult = listService.search(skuLsParams);
        model.addAttribute("SkuLsInfoList",skuLsResult.getSkuLsInfoList());
        //获取平台属性值
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> attrList = managerService.getAttrList(attrValueIdList);
        model.addAttribute("attrList",attrList);
        //已选的属性值
        List<BaseAttrValue> baseAttrValuesList = new ArrayList<>();
        String urlParam = makeUrlParam(skuLsParams);
        //用迭代器遍历
        for (Iterator<BaseAttrInfo> iterator = attrList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo baseAttrInfo =  iterator.next();
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setUrlParam(urlParam);
                if(skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
                    for (String valueId : skuLsParams.getValueId()) {
                        //选中的属性值 和 查询结果的属性值
                        if(valueId.equals(baseAttrValue.getId())){
                            iterator.remove();
                            // 构造面包屑列表
                            BaseAttrValue baseAttrValueSelected = new BaseAttrValue();
                            baseAttrValueSelected.setValueName(baseAttrInfo.getAttrName()+":"+baseAttrValue.getValueName());
                            // 去除重复数据
                            String makeUrlParam = makeUrlParam(skuLsParams, valueId);
                            baseAttrValueSelected.setUrlParam(makeUrlParam);
                            baseAttrValuesList.add(baseAttrValueSelected);
                        }
                    }
                }
            }
        }
        model.addAttribute("baseAttrValuesList",baseAttrValuesList);
        model.addAttribute("keyword",   skuLsParams.getKeyword());
        model.addAttribute("urlParam",urlParam);
        model.addAttribute("attrList",attrList);
        // 获取sku属性值列表
        List<SkuLsInfo> skuLsInfoList = skuLsResult.getSkuLsInfoList();
        model.addAttribute("skuLsInfoList",skuLsInfoList);
        model.addAttribute("totalPages",skuLsResult.getTotalPages());
        model.addAttribute("pageNo",skuLsParams.getPageNo());
        return "list";
    }

    //拼接历史条件
    public String makeUrlParam(SkuLsParams skuLsParam,String... excludeValueIds){
        String urlParam="";
        if(skuLsParam.getKeyword()!=null){
            urlParam+="keyword="+skuLsParam.getKeyword();
        }
        if (skuLsParam.getCatalog3Id()!=null){
            if (urlParam.length()>0){
                urlParam+="&";
            }
            urlParam+="catalog3Id="+skuLsParam.getCatalog3Id();
        }
        // 构造属性参数
        if (skuLsParam.getValueId()!=null && skuLsParam.getValueId().length>0){
            for (int i=0;i<skuLsParam.getValueId().length;i++){
                String valueId = skuLsParam.getValueId()[i];
                if (excludeValueIds!=null && excludeValueIds.length>0){
                    String excludeValueId = excludeValueIds[0];
                    if (excludeValueId.equals(valueId)){
                        // 跳出代码，后面的参数则不会继续追加【后续代码不会执行】
                        continue;
                    }
                }
                if (urlParam.length()>0){
                    urlParam+="&";
                }
                urlParam+="valueId="+valueId;
            }
        }
        return  urlParam;
    }
}
