package com.gy.gmall.service;

import com.gy.gmall.bean.*;

import java.util.List;

public interface ManagerService {
    public List<BaseCatalog1> getCatalog1();

    public List<BaseCatalog2> getCatalog2(String catalog1Id);

    public List<BaseCatalog3> getCatalog3(String catalog2Id);

    public List<BaseAttrInfo> getAttrList(String catalog3Id);

    public void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    public BaseAttrInfo getAttrInfo(String attrId);

    public void deleteAttrInfo(String id);

    public void deleteAttrValue(String attrid);


    /*
     * Spu属性操作
     * @param spuInfo
   */

    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    public void updateSpuInfo(SpuInfo spuInfo);

    public void deleteSpuInfo(SpuInfo spuInfo);

    List<BaseSaleAttr> getBaseSaleAttrList();

    public void saveSpuInfo(SpuInfo spuInfo);

    /*
     * 图片操作
  */

    //图片添加
    public void saveSpuImage(SpuImage spuImage);
    //获取图片信息
    public List<SpuImage> getSpuImage(String id) ;

    public void updateSpuImage(SpuImage spuImage);

    public void deleteSpuImage(SpuImage spuImage);

    /*
     * 销售属性操作
     */
    public List<SpuSaleAttr> getSpuSaleAttr();

    public List<SpuSaleAttr> getSpuSaleAttr(String spuId);

    public void saveSpuSaleAttr(SpuSaleAttr spuSaleAttr);

    public void updateSpuSaleAttr(SpuSaleAttr spuSaleAttr);

    public void deleteSpuSaleAttr(SpuSaleAttr spuSaleAttr);

    /**
     *  销售属性值
      */


    public List<SpuSaleAttrValue> getSpuSaleAttrValue(String id);


    public void saveSpuSaleAttrValue(SpuSaleAttrValue spuSaleAttrValue);

    public void updateSpuSaleAttrValue(SpuSaleAttrValue spuSaleAttrValue);

    public void deleteSpuSaleAttrValue(SpuSaleAttrValue spuSaleAttrValue);


    //保存SKUINFO
    public void saveSkuInfo(SkuInfo skuInfo);


}
