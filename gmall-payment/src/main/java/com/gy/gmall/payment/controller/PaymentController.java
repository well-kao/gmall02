package com.gy.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gy.gmall.bean.OrderInfo;
import com.gy.gmall.bean.PaymentInfo;
import com.gy.gmall.bean.enums.PaymentStatus;
import com.gy.gmall.config.LoginRequire;
import com.gy.gmall.payment.config.AlipayConfig;
import com.gy.gmall.service.OrderService;
import com.gy.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    @Reference
    OrderService orderService;
    @Reference
    PaymentService paymentService;
    @Autowired
    AlipayClient alipayClient;
    @LoginRequire(autoRedirect = true)
    @RequestMapping("/index")
    public String index(HttpServletRequest request,Model model){
        String orderId = request.getParameter("orderId");
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        model.addAttribute("orderId",orderId);
        model.addAttribute("totalAmount",orderInfo.getTotalAmount());
        return "index";
    }

    //就如支付宝接口实现支付
    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String submitPayment(HttpServletRequest request,HttpServletResponse response){
        String orderId = request.getParameter("orderId");
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(orderId);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);

        paymentService.savePaymentInfo(paymentInfo);
        //支付宝相关
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        //设置回跳函数
        alipayTradePagePayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        //设置通知地址
        alipayTradePagePayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        //用map将付款页面所需参数保存起来传入阿里的支付请求中
        Map<String,Object> map = new HashMap<>();
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",orderInfo.getTotalAmount());
        map.put("subject",paymentInfo.getSubject());
        // 将map转换成字符串
        String mapJson = JSON.toJSONString(map);
        alipayTradePagePayRequest.setBizContent(mapJson);
        // alipayRequest所有参数生产一个html 页面.
        String form="";
        try {
            form = alipayClient.pageExecute(alipayTradePagePayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=utf-8");
        // 将form 显示到页面
        return form;
    }

    //回调页面
    @RequestMapping(value = "/alipay/callback/return",method = RequestMethod.GET)
    public String callbackReturn(){
        return "redirect://"+AlipayConfig.return_order_url;
    }

    //异步回调
    @RequestMapping(value = "/alipay/callback/notify",method = RequestMethod.POST)
    @ResponseBody
    public String paymentNotify(@RequestParam Map<String,String> paramMap, HttpServletRequest request) throws AlipayApiException {
        // 拿公用key+数据验证
        String sign = request.getParameter("sign");
        boolean flag = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, "utf-8",AlipayConfig.sign_type);

        if (!flag){
            return "fial";
        }
        // 判断结束
        String trade_status = paramMap.get("trade_status");
        if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)){
            // 查单据是否处理
            String out_trade_no = paramMap.get("out_trade_no");
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOutTradeNo(out_trade_no);
            PaymentInfo paymentInfoHas = paymentService.getpaymentInfo(paymentInfo);

            if (paymentInfo.getPaymentStatus()==PaymentStatus.PAID || paymentInfo.getPaymentStatus()==PaymentStatus.ClOSED){
                return "fail";
            }else {
                // 修改
                PaymentInfo paymentInfoUpd = new PaymentInfo();
                // 设置状态
                paymentInfoUpd.setPaymentStatus(PaymentStatus.PAID);
                // 设置创建时间
                paymentInfoUpd.setCallbackTime(new Date());
                // 设置内容
                paymentInfoUpd.setCallbackContent(paramMap.toString());
                paymentService.updatePaymentInfo(out_trade_no,paymentInfoUpd);
                return "success";
            }
        }
        return  "fail";
    }
}
