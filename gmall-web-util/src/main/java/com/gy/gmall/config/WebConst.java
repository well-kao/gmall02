package com.gy.gmall.config;

/**
 * @param
 * @return
 */
public class WebConst {

//    设置cookie 的有效时间
    public static final int COOKIE_MAXAGE=7*24*3600;
//      验证当前用户是否登录url
    public static  final  String VERIFY_ADDRESS="http://passport.atguigu.com/verify";
//      如果用户没有登录，则进行重新登录
    public static  final  String LOGIN_ADDRESS="http://passport.atguigu.com/index";


}
