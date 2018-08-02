package com.gy.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.gmall.bean.*;
import com.gy.gmall.service.ManagerService;
import com.sun.corba.se.impl.logging.POASystemException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ManageController {
    @Reference
    private ManagerService managerService;

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    //点击平台属性就弹出平台管理
    @RequestMapping("/attrListPage")
    public String getAttrListPage() {
        return "attrListPage";
    }

    //点击商品spu管理就弹出spu管理
    @RequestMapping("/spuListPage")
    public String getSpuListPage() {
        return "spuListPage";
    }

    /**
     * 显示平台属性管理数据信息
     */
    //一级菜单
    @RequestMapping("/getCatalog1")
    @ResponseBody
    public List<BaseCatalog1> getBaseCatalog1Info() {
        return managerService.getCatalog1();

    }

    //  二级菜单
    @RequestMapping("/getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getBaseCatalog2(String catalog1Id) {
        return managerService.getCatalog2(catalog1Id);
    }

    //三级菜单
    @RequestMapping("/getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getBaseCatalog3(String catalog2Id) {
        return managerService.getCatalog3(catalog2Id);
    }

    //展示平台属性
    @RequestMapping("/getAttrList")
    @ResponseBody
    public List<BaseAttrInfo> getBaseAttrInfo(String catalog3Id) {
        return managerService.getAttrList(catalog3Id);
    }

    //添加修改平台属性
    @RequestMapping(value = "/saveAttrInfo", method = RequestMethod.POST)
    @ResponseBody
    public String saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        managerService.saveAttrInfo(baseAttrInfo);
        return "success";
    }

    //展示平台属性值
    @RequestMapping(value = "/getAttrValueList", method = RequestMethod.POST)
    @ResponseBody
    public List<BaseAttrValue> getAttrValueInfo(String attrId) {
        BaseAttrInfo attrInfo = managerService.getAttrInfo(attrId);
        List<BaseAttrValue> attrValueList = attrInfo.getAttrValueList();
        return attrValueList;
    }

    //删除平台属性
    @ResponseBody
    @RequestMapping("/deleteAttrInfo")
    public String deleteAttrInfo(String id) {
        managerService.deleteAttrInfo(id);
        managerService.deleteAttrValue(id);
        return "success";
    }

    @ResponseBody
    @RequestMapping("/deleteAttrValue")
    public String deleteAttrValue(String id) {
        managerService.deleteAttrValue(id);
        return "success";
    }

    /**
     * 商品SPU管理
     */

    //获取spu属性列表
    @RequestMapping("/getSpuInfo")
    @ResponseBody
    public List<SpuInfo> getSpuInfo(String catalog3Id) {
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        return managerService.getSpuInfoList(spuInfo);
    }

    //获取销售属性
    @RequestMapping(value="/getBaseSaleAttr",method = RequestMethod.POST)
    @ResponseBody
    public List<BaseSaleAttr> getBaseSaleAttr() {
        return managerService.getBaseSaleAttrList();
    }

    //保存SpuInfo
    @RequestMapping(value = "/saveSpuInfo", method = RequestMethod.POST)
    @ResponseBody
    public String saveSpuInfo(SpuInfo spuInfo) {
        managerService.saveSpuInfo(spuInfo);
        return "success";
    }

}