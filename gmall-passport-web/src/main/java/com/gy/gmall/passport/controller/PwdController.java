package com.gy.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.gmall.bean.UserInfo;
import com.gy.gmall.passport.util.JwtUtil;
import com.gy.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PwdController {

    @Value("${token.key")
    String signKey;
    @Reference
    UserInfoService userInfoService;
    //单点登录控制器
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public String login(HttpServletRequest request, UserInfo userInfo){
        //获取IP地址
        String ip = request.getHeader("X-forwarded-for");
        if(userInfo!=null){
            UserInfo loginUser = userInfoService.login(userInfo);
            if(loginUser==null){
                return "fail";
            }else{
                //生成token
                Map<String,Object> map  =new HashMap<>();
                map.put("userId",loginUser.getId());
                map.put("nickName",loginUser.getNickName());
                String token = JwtUtil.encode(signKey,map,ip);
                return token;
            }

        }
        return  "fail";
    }

    //登录认证，去缓存查数据
    @RequestMapping("/verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        String token = request.getParameter("token");
        String currentIp = request.getParameter("currentIp");
        //解码
        Map<String, Object> decode = JwtUtil.decode(token, currentIp, signKey);
        if(decode!=null){
            String userId = (String) decode.get("userId");

        }
        return null;
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}
