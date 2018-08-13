package com.gy.gmall.service;

import com.gy.gmall.bean.CartInfo;

import java.util.List;

public interface CartInfoService {
    void addToCart(String skuId,String userId,Integer skuNum);

    //从redis中获取购物车列表
    List<CartInfo> loadCartCache(String userId);
    //从数据库获取购物车列表
    List<CartInfo> getCartList(String userId);
    //合并购物车
    List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId);
    //选中购物车
    void checkCart(String skuId, String isChecked, String userId);
    //获取购物车列表
    List<CartInfo> getCartCheckedList(String userId);
}
