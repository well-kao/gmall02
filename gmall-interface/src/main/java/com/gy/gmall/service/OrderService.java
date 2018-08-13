package com.gy.gmall.service;

import com.gy.gmall.bean.OrderInfo;

public interface OrderService {
    //保存订单，方便跳转支付界面执行操作
    String saveOrder(OrderInfo orderInfo);

    //生成流水号
    String getTradeCode(String userId);
    //验证流水号
    boolean checkTradeCode(String userId,String tradeCode);
    //删除流水号
    void deleteTradeCode(String userId);

    //验证库存
    boolean checkStock(String skuId, Integer skuNum);

    //获取订单信息
    OrderInfo getOrderInfo(String orderId);
}
