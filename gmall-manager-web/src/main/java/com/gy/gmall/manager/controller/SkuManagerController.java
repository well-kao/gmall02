package com.gy.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.gmall.bean.BaseAttrInfo;
import com.gy.gmall.bean.SkuInfo;
import com.gy.gmall.bean.SpuImage;
import com.gy.gmall.bean.SpuSaleAttr;
import com.gy.gmall.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SkuManagerController {
    @Reference
    private ManagerService  managerService;

    //获取SPU图片
    @ResponseBody
    @RequestMapping(value="/spuImageList")
    public List<SpuImage> getSpuImage(String spuId){
        List<SpuImage> spuImage = managerService.getSpuImage(spuId);
        return spuImage;
    }

    //获取平台属性
    @RequestMapping("/attrInfoList")
    @ResponseBody
    public List<BaseAttrInfo> getBaseAttr(String catalog3Id){

        return managerService.getAttrList(catalog3Id);
    }
    //获取销售属性
    @RequestMapping(value = "/spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> spuSaleAttrList(String spuId){
        List<SpuSaleAttr> spuSaleAttrs =  managerService.getSpuSaleAttr(spuId);
        return spuSaleAttrs;
    }
    //skuInfo大保存
    @RequestMapping("/saveSku")
    @ResponseBody
    public String saveSkuInfo(SkuInfo skuInfo){
        managerService.saveSkuInfo(skuInfo);
        return "success";

    }
}
