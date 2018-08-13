package com.gy.gmall.config;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class AuthInterceptor extends  HandlerInterceptorAdapter{

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 先获取token数据 只有登录的时候，才会产生
        String token = request.getParameter("newToken");
        if (token!=null){
            // 将token放入cookie中
            CookieUtil.setCookie(request,response,"token",token,WebConst.COOKIE_MAXAGE,false);
        }
        if (token==null){
            // 从cookie中取得数据
            token=CookieUtil.getCookieValue(request,"token",false);
        }
        //  再判断有没有取得到token : 对token进行解密
        if (token!=null){
            // 解密token
            Map map = getUserMapByToken(token);
            // map.get(); 取得到nickName
            String nickName = (String) map.get("nickName");
            // 将用户的昵称，保存到作用域中，为了在页面显示
            request.setAttribute("nickName",nickName);
        }
        //  做点手脚，获取到当前控制器的方法的注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 如果该类上没有LoginRequire,那么loginRequireAnnotation 返回null 要想获取到userId ，则在方法上需要添加LoginRequire注解！
        LoginRequire loginRequireAnnotation  = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (loginRequireAnnotation!=null){
            // 调用认证方法 http://passprot.atguigu.com/verify?token=xxxx&currentIp=xxx
            // http://passport.atguigu.com/verify
            // 先获取ip地址 192.168.67.1
            String remoteAddr = request.getHeader("x-forwarded-for");
            // 认证方法在passportController中，则需要使用远程调用认证
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + remoteAddr);
            if ("success".equals(result)){
                // 成功，保存一个userId，为了明天的购物车使用！
                Map map = getUserMapByToken(token);
                String userId = (String) map.get("userId");
                request.setAttribute("userId",userId);
                return true;
            }else {
                    if (loginRequireAnnotation.autoRedirect()){
                        // 获取url getRequestURI(); requestURL
                        String requestURL = request.getRequestURL().toString();
                        // http%3A%2F%2Fitem.gmall.com%2F28.html 转换成utf-8
                        String encodeURL  = URLEncoder.encode(requestURL, "UTF-8");
                        // 让用户进行登录
                        response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);
                        return false;
                    }
            }
        }
        return true;
    }
//  eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6IkFkbWluaXN0cmF0b3IiLCJ1c2VySWQiOiIyIn0.WUvbFvXQnTMBGNyHWT-DE41MR9cn7c_W1oAtDAzb7VU
//    token 分为三部分：
    private Map getUserMapByToken(String token) {
//    先将token进行分割
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
//    解密不用jwtUtil。
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] decode = base64UrlCodec.decode(tokenUserInfo);
//     将字节数组转换成字符串
        String newUserInfo =null;
        try {
            newUserInfo = new String(decode,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//      将字符串转换成对象
        Map map = JSON.parseObject(newUserInfo, Map.class);
        return map;
    }
}
