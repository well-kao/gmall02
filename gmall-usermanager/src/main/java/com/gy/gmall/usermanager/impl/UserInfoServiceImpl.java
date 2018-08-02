package com.gy.gmall.usermanager.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.gmall.bean.UserInfo;
import com.gy.gmall.service.UserInfoService;
import com.gy.gmall.usermanager.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public List<UserInfo> getAllUserInfo() {

        return userInfoMapper.selectAll();
    }
}
