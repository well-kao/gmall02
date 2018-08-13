package com.gy.gmall.manager.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.gmall.bean.*;
import com.gy.gmall.manager.mapper.*;
import com.gy.gmall.service.ManagerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Service
public class ManageServiceImpl implements ManagerService {

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    FileUploadMapper fileUploadMapper;
    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    SpuImageMapper spuImageMapper;
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    skuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    SkuImageMapper skuImageMapper;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);

        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        return baseAttrInfoMapper.getBaseAttrInfoByCatalog3Id(catalog3Id);

    }
    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        String attrValueIds = StringUtils.join(attrValueIdList.toArray(), ",");
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectAttrInfoListByIds(attrValueIds);
        return baseAttrInfoList;

    }


    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        System.out.println(baseAttrInfo);
        if(baseAttrInfo.getId()!=null&&baseAttrInfo.getId().length()>0){
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        }else{
            if(baseAttrInfo.getId()==null ||baseAttrInfo.getId().length()==0){
                baseAttrInfo.setId(null);
            }
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
            //增改平台属性值一体
        //把旧数据删除再添加
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValue);
        if(baseAttrInfo.getAttrValueList()!=null&&baseAttrInfo.getAttrValueList().size()>0){
            for (BaseAttrValue attrValue :baseAttrInfo.getAttrValueList() ) {
                if(attrValue.getId().length()==0){
                    attrValue.setId(null);
                }
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(attrValue);
            }
        }
    }

    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(baseAttrInfo.getId());
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
        baseAttrInfo.setAttrValueList(baseAttrValues);
        return baseAttrInfo;
    }

    @Override
    public void deleteAttrInfo(String id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setId(id);
        baseAttrInfoMapper.delete(baseAttrInfo);
    }

    @Override
    public void deleteAttrValue(String attrid) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrid);
        baseAttrValueMapper.delete(baseAttrValue);

    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {

        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public void updateSpuInfo(SpuInfo spuInfo) {

    }

    @Override
    public void deleteSpuInfo(SpuInfo spuInfo) {

    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        if(spuInfo.getId().length()==0){
            spuInfo.setId(null);
            System.out.println("商品ID是："+spuInfo.getId());
           spuInfoMapper.insertSelective(spuInfo);
        }else{
            spuInfoMapper.updateByPrimaryKey(spuInfo);
    }
        System.out.println("商品的ID是："+spuInfo.getId());
        //保存图片信息
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuInfo.getId());
        fileUploadMapper.delete(spuImage);
        //真正的添加图片

        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage image : spuImageList) {
            if (image.getId() != null && image.getId().length() == 0) {
                image.setId(null);
            }
            image.setSpuId(spuInfo.getId());
            fileUploadMapper.insertSelective(image);

            }
            //删除销售属性和属性值
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuInfo.getId());
        spuSaleAttrMapper.delete(spuSaleAttr);
        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
        spuSaleAttrValue.setSpuId(spuInfo.getId());
        spuSaleAttrValueMapper.delete(spuSaleAttrValue);

            //  保存属性和属性值
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr saleAttr : spuSaleAttrList) {
            if(saleAttr.getId()!=null&&saleAttr.getId().length()==0){
                saleAttr.setId(null);

            }
            saleAttr.setSpuId(spuInfo.getId());
            spuSaleAttrMapper.insertSelective(saleAttr);
            for (SpuSaleAttrValue saleAttrValue : saleAttr.getSpuSaleAttrValueList()) {
                if(saleAttrValue.getId()!=null&&saleAttrValue.getId().length()==0){
                    saleAttrValue.setId(null);
                }
                saleAttrValue.setSpuId(spuInfo.getId());
                spuSaleAttrValueMapper.insertSelective(saleAttrValue);
            }
        }
    }



    @Override
    public void saveSpuImage(SpuImage spuImage) {

    }

    @Override
    public List<SpuImage> getSpuImage(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        return spuImageMapper.select(spuImage);
    }

    @Override
    public void updateSpuImage(SpuImage spuImage) {

    }

    @Override
    public void deleteSpuImage(SpuImage spuImage) {

    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttr() {
        return null;
    }

   //根据spuid查询销售属性
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(String spuId) {
        return spuSaleAttrMapper.selectBySpuId(spuId);
    }

    @Override
    public void saveSpuSaleAttr(SpuSaleAttr spuSaleAttr) {

    }

    @Override
    public void updateSpuSaleAttr(SpuSaleAttr spuSaleAttr) {

    }

    @Override
    public void deleteSpuSaleAttr(SpuSaleAttr spuSaleAttr) {

    }

    @Override
    public List<SpuSaleAttrValue> getSpuSaleAttrValue(String id) {
        return null;
    }

    @Override
    public void saveSpuSaleAttrValue(SpuSaleAttrValue spuSaleAttrValue) {

    }

    @Override
    public void updateSpuSaleAttrValue(SpuSaleAttrValue spuSaleAttrValue) {

    }

    @Override
    public void deleteSpuSaleAttrValue(SpuSaleAttrValue spuSaleAttrValue) {

    }
    /*
    *SkuInfo操作
     */
    //保存SkuInfo
   @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        if (skuInfo.getId() == null || skuInfo.getId().length() == 0) {
            skuInfo.setId(null);
            skuInfoMapper.insertSelective(skuInfo);
        } else {
            skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
        }
        //保存平台属性
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuInfo.getId());
        skuAttrValueMapper.delete(skuAttrValue);
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : skuAttrValueList) {
            if (attrValue.getId() != null && attrValue.getId().length() == 0) {
                attrValue.setId(null);
            }
            attrValue.setSkuId(skuInfo.getId());
            skuAttrValueMapper.insertSelective(attrValue);
        }
        //保存销售属性
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuInfo.getId());
        skuSaleAttrValueMapper.delete(skuSaleAttrValue);

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
            if(saleAttrValue.getId()!=null&&saleAttrValue.getId().length()==0){
                saleAttrValue.setId(null);
            }
            saleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValueMapper.insertSelective(saleAttrValue);
        }

        //保存图片
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuInfo.getId());
        skuImageMapper.delete(skuImage);
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage image : skuImageList) {
            if(image.getId()!=null&&image.getId().length()==0){
                image.setId(null);
            }
            image.setSkuId(skuInfo.getId());
            skuImageMapper.insertSelective(image);
        }
    }

    }
