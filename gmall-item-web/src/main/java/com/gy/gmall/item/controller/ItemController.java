package com.gy.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gy.gmall.bean.SkuInfo;
import com.gy.gmall.bean.SkuSaleAttrValue;
import com.gy.gmall.bean.SpuSaleAttr;
import com.gy.gmall.service.ItemService;
import com.gy.gmall.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    ManagerService managerService;
    @Reference
    ItemService itemService;
    @RequestMapping("/{skuid}.html")
    public String getSkuInfo(@PathVariable("skuid")String skuId,Model model)
    {
        SkuInfo skuInfo = itemService.getSkuInfo(skuId);
        model.addAttribute("skuInfo",skuInfo);
        List<SpuSaleAttr> spuSaleAttr = itemService.getSpuSaleAttr(skuInfo);
        model.addAttribute("spuSaleAttr",spuSaleAttr);
        List<SkuSaleAttrValue> skuSaleAttrValues =
                itemService.getSkuSaleAttrValue(skuInfo.getSpuId());
        String jsonKey = "";
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValues.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValues.get(i);
            // 当jsonKey 不为空的时候
            if (jsonKey.length()!=0){
                jsonKey+="|";
            }
            jsonKey=jsonKey+skuSaleAttrValue.getSaleAttrValueId();
            // 什么时候停止当前的字符串拼接
            if ((i+1)==skuSaleAttrValues.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValues.get(i+1).getSkuId())){
                map.put(jsonKey,skuSaleAttrValue.getSkuId());
                // 将字符串清空
                jsonKey="";
            }
        }
        // 将map集合转换成json对象
        String valuesSkuJson  = JSON.toJSONString(map);

        // 将字符串信息保存到后台，前台取得数据进行匹配 一组相关的spuId 对应的销售属性值以及skuId {"91|94":27, "91|93":28}

        model.addAttribute("valuesSkuJson",valuesSkuJson);
        return "item";
    }
}
