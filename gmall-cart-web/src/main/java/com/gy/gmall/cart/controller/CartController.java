package com.gy.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.gmall.bean.CartInfo;
import com.gy.gmall.bean.SkuInfo;
import com.gy.gmall.config.LoginRequire;
import com.gy.gmall.service.CartInfoService;
import com.gy.gmall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
public class CartController {

    @Reference
    CartInfoService cartInfoService;

    @Reference
    ItemService itemService;

    @Autowired
    CartCookieHandler cartCookieHandler;

    @RequestMapping("/addToCart")
    @LoginRequire(autoRedirect = false)//不需要登陆
    public String addToCart(HttpServletRequest request, HttpServletResponse response) {
        String skuId = request.getParameter("skuId");
        String skuNum = request.getParameter("skuNum");
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            cartInfoService.addToCart(skuId, userId, Integer.parseInt(skuNum));
        } else {
            //如果用户没有登录就将购物车信息存入cookie中
            cartCookieHandler.addToCart(request, response, skuId, userId, Integer.parseInt(skuNum));
        }
        SkuInfo skuInfo = itemService.getSkuInfo(skuId);
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("skuNum", skuNum);
        return "success";
    }

    //展示购物车信息页面

    /*public String cartList(HttpServletRequest request,HttpServletResponse response){
        // 判断用户是否登录，登录了从redis中，redis中没有，从数据库中取
        String userId = (String) request.getAttribute("userId");
        if(userId!=null){
            // 从cookie中查找购物车
            List<CartInfo> cartListFromCookie = cartCookieHandler.getCartList(request);
            List<CartInfo> cartList = null;
            if (cartListFromCookie!=null && cartListFromCookie.size()>0){
                // 开始合并
                cartInfoService.mergeToCartList(cartListFromCookie,userId);
                // 删除cookie中的购物车
                cartCookieHandler.deleteCartCookie(request,response);
            }else{
                // 从redis中取得，或者从数据库中
                cartList= cartInfoService.getCartList(userId);
            }
            request.setAttribute("cartList",cartList);
        }else{
            List<CartInfo> cartList = cartCookieHandler.getCartList(request);
            request.setAttribute("cartList",cartList);
        }
        return "cartList";
    }*/

    //合并购物车
    @RequestMapping("/cartList")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            List<CartInfo> cartListCk = cartCookieHandler.getCartList(request);
            List<CartInfo> cartInfoList = null;
            if (cartListCk != null && cartListCk.size() > 0) {
                //cookie中有的话就跟数据库合并
                cartInfoService.mergeToCartList(cartListCk, userId);
                //删除cookie中数据
                cartCookieHandler.deleteCartCookie(request, response);
            } else {
                //cookie中没有的话直接查数据库或者从Redis中获取
                cartInfoList = cartInfoService.getCartList(userId);
            }
            request.setAttribute("cartList", cartInfoList);
        } else {
            //未登录直接从cookie中获取然后插入数据库
            List<CartInfo> cartList = cartCookieHandler.getCartList(request);
            request.setAttribute("cartList", cartList);
        }
        return "cartList";
    }

    //选定购物车中的商品
    @RequestMapping("checkCart")
    @ResponseBody
    @LoginRequire(autoRedirect = false)
    public void checkCart(HttpServletRequest request, HttpServletResponse response) {
        String skuId = request.getParameter("skuId");
        String isChecked = request.getParameter("isChecked");
        String userId = (String) request.getAttribute("userId");
        if(userId!=null){
            cartInfoService.checkCart(skuId,isChecked,userId);
        }else {
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }
    }
    //点击结算跳转结算界面
    //用户未登录时跳转登陆，然后合并
    @RequestMapping("/toTrade")
    @LoginRequire(autoRedirect = true)
    public String toTrade(HttpServletResponse response,HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartListFromCk = cartCookieHandler.getCartList(request);
        if(cartListFromCk!=null&&cartListFromCk.size()>0){
            cartInfoService.mergeToCartList(cartListFromCk, userId);
            cartCookieHandler.deleteCartCookie(request,response);
        }
        return "redirect://order.gmall.com/trade";

    }
}