package com.gy.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.gmall.bean.OrderDetail;
import com.gy.gmall.bean.OrderInfo;
import com.gy.gmall.config.HttpClientUtil;
import com.gy.gmall.config.RedisUtil;
import com.gy.gmall.order.mapper.OrderDetailMapper;
import com.gy.gmall.order.mapper.OrderInfoMapper;
import com.gy.gmall.service.OrderService;
import org.apache.http.client.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    RedisUtil redisUtil;


    //保存订单
    @Override
    public String saveOrder(OrderInfo orderInfo) {
        //创建保存时间
        orderInfo.setCreateTime(new Date());
        //设置订单失效时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        orderInfo.setExpireTime(calendar.getTime());

        String tradeNo = new Random().nextInt(1000)+System.currentTimeMillis()+"";
        orderInfo.setOutTradeNo(tradeNo);
        orderInfoMapper.insertSelective(orderInfo);

        //插入订单项请
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insertSelective(orderDetail);

        }
        String orderId = orderInfo.getId();
        //将订单ID返回方便跳转结算操作的url中拼接参数
        return orderId;
    }

    @Override
    public String getTradeCode(String userId) {

        Jedis jedis = redisUtil.getJedis();
        String tradeKey = "user:"+userId+":tradeCode";
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex(tradeKey,10*60,tradeCode);
        jedis.close();
        return tradeCode;
    }

    @Override
    public boolean checkTradeCode(String userId,String tradeCode) {

        Jedis jedis = redisUtil.getJedis();
        String tradeKry = "user:"+userId+":tradeCode";
        String tradeCodeFromRd = jedis.get(tradeKry);
        jedis.close();
        if(tradeCodeFromRd!=null&&tradeCode.equals(tradeCodeFromRd)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void deleteTradeCode(String userId) {

        Jedis jedis = redisUtil.getJedis();
        String tradeKey =  "user:"+userId+":tradeCode";
        jedis.del(tradeKey);
        jedis.close();
    }

    //验证库存
    @Override
    public boolean checkStock(String skuId, Integer skuNum) {
        String result = HttpClientUtil.doGet("http://www.gware.com/hasStock?skuId=" + skuId + "&num=" + skuNum);
        if("1".equals(result)){
            return  true;
        }else {
            return false;
        }
    }

    //获取订单信息
    @Override
    public OrderInfo getOrderInfo(String orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderId);
        return orderInfo;
    }


}
