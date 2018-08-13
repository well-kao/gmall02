package com.gy.gmall.usermanager.controller;

import com.gy.gmall.bean.UserInfo;
import com.gy.gmall.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @ResponseBody
    @GetMapping("/getAllUserInfo")
    public List<UserInfo> getAllUserInfo(){
        List<UserInfo> allUserInfo = userInfoService.getAllUserInfo();
        return allUserInfo;
    }
}
