package com.gy.gmall.order.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.gmall.bean.CartInfo;
import com.gy.gmall.bean.OrderDetail;
import com.gy.gmall.bean.OrderInfo;
import com.gy.gmall.bean.UserAddress;
import com.gy.gmall.bean.enums.OrderStatus;
import com.gy.gmall.bean.enums.ProcessStatus;
import com.gy.gmall.config.LoginRequire;
import com.gy.gmall.service.CartInfoService;
import com.gy.gmall.service.OrderService;
import com.gy.gmall.service.UserAddressService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    UserAddressService userAddressService;
    @Reference
    CartInfoService cartInfoService;
    @Reference
    OrderService orderService;
//获取选中的购物车列表
    @RequestMapping("/trade")
    @LoginRequire(autoRedirect = true)
    public String trade(HttpServletRequest request, HttpServletResponse response){
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartCheckedList = cartInfoService.getCartCheckedList(userId);
        List<OrderDetail> orderDetails = new ArrayList<>(cartCheckedList.size());
        for (CartInfo cartInfo : cartCheckedList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetails.add(orderDetail);

        }
        request.setAttribute("orderDetailList",orderDetails);
        List<UserAddress> userAddressList = userAddressService.getAllUserAddress(userId);
        request.setAttribute("userAddressList",userAddressList);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetails);
        //调用计算总价的方法
        orderInfo.sumTotalAmount();
        request.setAttribute("totalAmount",orderInfo.getTotalAmount());
        String tradeCode = orderService.getTradeCode(userId);
        request.setAttribute("tradeNo",tradeCode);
        return "trade";
    }

    //提交订单
    @LoginRequire(autoRedirect = true)
    @RequestMapping("/submitOrder")
    public String submitOrder(OrderInfo orderInfo,HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        String tradeNo = request.getParameter("tradeNo");
        boolean flag = orderService.checkTradeCode(userId,tradeNo);
        if(!flag){
            request.setAttribute("errMsg","页面失效，请重新结算");

            return "tradeFail";

        }
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.sumTotalAmount();
        orderInfo.setUserId(userId);
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            boolean result = orderService.checkStock(orderDetail.getSkuId(),orderDetail.getSkuNum());
            if(!result){
                request.setAttribute("errMsg","库存不足，请选择其他商品");
                return "tradeFail";
            }
        }
        String orderId = orderService.saveOrder(orderInfo);
        return "redirect://payment.gmall.com/index?orderId="+orderId;
    }

}
