package com.gy.gmall.usermanager.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.gmall.bean.UserAddress;
import com.gy.gmall.service.UserAddressService;
import com.gy.gmall.usermanager.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getAllUserAddress(String userId) {
        return null;
    }

    @Override
    public UserAddress getUserAddressByUserId(String userId) {
       UserAddress userAddress = new UserAddress();
       userAddress.setId(userId);
        return userAddressMapper.selectOne(userAddress);
    }
}
