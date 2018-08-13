package com.gy.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.gmall.bean.PaymentInfo;
import com.gy.gmall.payment.mapper.PaymentMapper;
import com.gy.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentMapper paymentMapper;
    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        PaymentInfo newPaymentInfo = new PaymentInfo();
        newPaymentInfo.setOrderId(paymentInfo.getOrderId());
        List<PaymentInfo> paymentInfoList = paymentMapper.select(newPaymentInfo);
        if(paymentInfoList.size()>0){
            return;
        }
        paymentMapper.insertSelective(paymentInfo);
    }

    @Override
    public PaymentInfo getpaymentInfo(PaymentInfo paymentInfo) {
        return   paymentMapper.selectOne(paymentInfo);
    }

    @Override
    public void updatePaymentInfo(String outTradeNo, PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("outTradeNo",outTradeNo);
        paymentMapper.updateByExampleSelective(paymentInfo,example);
    }
}
