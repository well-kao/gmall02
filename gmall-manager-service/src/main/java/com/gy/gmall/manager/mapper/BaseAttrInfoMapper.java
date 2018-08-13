package com.gy.gmall.manager.mapper;

import com.gy.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    List<BaseAttrInfo> getBaseAttrInfoByCatalog3Id(String catalog3Id);
    List<BaseAttrInfo> selectAttrInfoListByIds(@Param("valueIds") String valueIds);
}

