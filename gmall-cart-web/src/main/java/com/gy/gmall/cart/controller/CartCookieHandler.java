package com.gy.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gy.gmall.bean.CartInfo;
import com.gy.gmall.bean.SkuInfo;
import com.gy.gmall.config.CookieUtil;
import com.gy.gmall.service.ItemService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

//实现保存购物车的cookie的各种操作
@Component
public class CartCookieHandler {

    /*和service模块的方法类似
1、先查询出来在cookie中的购物车，反序列化成列表。
2、通过循环比较有没有该商品
3、如果有，增加数量
4、如果没有，增加商品
5、然后把列表反序列化，利用之前最好的CookieUtil保存到cookie中。*/

    // 定义购物车名称
    private String cookieCartName = "CART";
    // 设置cookie 过期时间
    private int COOKIE_CART_MAXAGE=7*24*3600;
    @Reference
    ItemService itemService;

    public void addToCart(HttpServletRequest request, HttpServletResponse response,String skuId,String userId,Integer skuNum){
        //判断是否有购物车数据，因为可能有中文所以要序列化
        String cartJson = CookieUtil.getCookieValue(request, cookieCartName, true);
        //将购物车信息存入List
        List<CartInfo> cartInfoList = new ArrayList<>();
        boolean ifExist = false;
        if(cartJson!=null){
            //将cartJson字符串转成对象数组传给cartInfo
            cartInfoList = JSON.parseArray(cartJson,CartInfo.class);
            for (CartInfo cartInfo : cartInfoList) {
                if(cartInfo.getSkuId().equals(skuId)){
                    //原购物车数量跟新添加购物车匹配商品数量合并
                    cartInfo.setSkuNum(cartInfo.getSkuNum()+skuNum);
                    //设置价格
                    cartInfo.setSkuPrice(cartInfo.getCartPrice());
                    ifExist=true;
                    break;
                }

            }
        }

        if(!ifExist){
            //购物车中没有对应商品
            //跟从商品中把信息保存到购物车商品信息的代码一致
            SkuInfo skuInfo = itemService.getSkuInfo(skuId);
            CartInfo cartInfo=new CartInfo();

            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());

            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);
            cartInfoList.add(cartInfo);
        }
        //存入cookie
        String newCartJson = JSON.toJSONString(cartInfoList);
        CookieUtil.setCookie(request,response,cookieCartName,newCartJson,COOKIE_CART_MAXAGE,true);

    }

    public List<CartInfo> getCartList(HttpServletRequest request) {
        String cartJson = CookieUtil.getCookieValue(request, cookieCartName, true);
        List<CartInfo> cartInfoList = JSON.parseArray(cartJson, CartInfo.class);
        return cartInfoList;
    }

    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,cookieCartName);
    }

    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        List<CartInfo> cartList = getCartList(request);
        for (CartInfo cartInfo : cartList) {
            if(skuId.equals(cartInfo.getSkuId())){
                cartInfo.setIsChecked(isChecked);
            }
        }
        String newCartJson = JSON.toJSONString(cartList);
        CookieUtil.setCookie(request,response,cookieCartName,newCartJson,COOKIE_CART_MAXAGE,true);



    }
}
