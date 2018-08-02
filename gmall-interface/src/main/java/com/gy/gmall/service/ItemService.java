package com.gy.gmall.service;

import com.gy.gmall.bean.SkuImage;
import com.gy.gmall.bean.SkuInfo;
import com.gy.gmall.bean.SkuSaleAttrValue;
import com.gy.gmall.bean.SpuSaleAttr;

import java.util.List;

public interface ItemService {

    //获取销售属性
    List<SpuSaleAttr> getSpuSaleAttr(SkuInfo skuInfo);
    //获取sku信息
    public SkuInfo getSkuInfo(String skuId);
    //获取页面选定的销售属性及绑定的skuId
    List<SkuSaleAttrValue> getSkuSaleAttrValue(String spuId);
}
