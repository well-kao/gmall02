package com.gy.gmall.service;

import com.gy.gmall.bean.UserAddress;

import java.util.List;

public interface UserAddressService {
    public List<UserAddress> getAllUserAddress(String userId);
    public UserAddress getUserAddressByUserId(String userId);
}
