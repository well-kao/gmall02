package com.gy.gmall.manager.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gy.gmall.bean.SkuInfo;
import com.gy.gmall.bean.SkuSaleAttrValue;
import com.gy.gmall.bean.SpuSaleAttr;
import com.gy.gmall.manager.mapper.SkuInfoMapper;
import com.gy.gmall.manager.mapper.SkuSaleAttrValueMapper;
import com.gy.gmall.manager.mapper.SpuSaleAttrMapper;
import com.gy.gmall.manager.mapper.SpuSaleAttrValueMapper;
import com.gy.gmall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService{
    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(SkuInfo skuInfo) {
        return spuSaleAttrMapper.getSpuSaleAttr(skuInfo.getId(),skuInfo.getSpuId());
    }

    @Override
        public SkuInfo getSkuInfo(String skuId) {
        return skuInfoMapper.selectByPrimaryKey(skuId);
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValue(String spuId) {
        return skuSaleAttrValueMapper.getSkuSaleAttrValue(spuId);
    }
}
