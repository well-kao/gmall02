package com.gy.gmall.service;

import com.gy.gmall.bean.SkuLsInfo;
import com.gy.gmall.bean.SkuLsParams;
import com.gy.gmall.bean.SkuLsResult;

public interface ListService {

    public void saveSkuInfo(SkuLsInfo skuLsInfo);

    SkuLsResult search(SkuLsParams skuLsParams);
}
