package com.gy.gmall.service;

import com.gy.gmall.bean.PaymentInfo;

public interface PaymentService {
    void  savePaymentInfo(PaymentInfo paymentInfo);

    PaymentInfo getpaymentInfo(PaymentInfo paymentInfo);

    void updatePaymentInfo(String outTradeNo, PaymentInfo paymentInfo);
}
