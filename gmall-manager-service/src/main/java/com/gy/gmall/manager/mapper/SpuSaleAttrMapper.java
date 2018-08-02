package com.gy.gmall.manager.mapper;

import com.gy.gmall.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    List<SpuSaleAttr> selectBySpuId(String spuId);

    List<SpuSaleAttr> getSpuSaleAttr(String skuId,String spuId);
}
