package com.gy.gmall.manager.mapper;

import com.gy.gmall.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {
    List<SkuSaleAttrValue> getSkuSaleAttrValue(String spuId);
}
