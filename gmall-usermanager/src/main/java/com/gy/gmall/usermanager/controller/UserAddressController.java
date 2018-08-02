package com.gy.gmall.usermanager.controller;

import com.gy.gmall.bean.UserAddress;
import com.gy.gmall.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserAddressController {
    @Autowired
    private UserAddressService userAddressService;

    @ResponseBody
    @RequestMapping(value = "/getUserAddressByUserId/{userId}",method = RequestMethod.POST)
    public UserAddress getUserAddressByUserId(@PathVariable("userId") String userId){
        return userAddressService.getUserAddressByUserId(userId);
    }

}
