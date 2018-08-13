package com.gy.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.gmall.bean.*;
import com.gy.gmall.service.ItemService;
import com.gy.gmall.service.ListService;
import com.gy.gmall.service.ManagerService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
public class SkuManagerController {
    @Reference
    private ManagerService  managerService;
    @Reference
    ItemService itemService;
    @Reference
    ListService listService;

    //商品上架，也是保存saveSkuLsList
    @RequestMapping("/onSale")
    @ResponseBody
    public void sendSkuOnSale(String skuId) throws InvocationTargetException, IllegalAccessException {
        SkuInfo skuInfo = itemService.getSkuInfo(skuId);
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        BeanUtils.copyProperties(skuLsInfo,skuInfo);
        listService.saveSkuInfo(skuLsInfo);
    }

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
