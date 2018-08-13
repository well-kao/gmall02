package com.gy.gmall.usermanager.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gy.gmall.bean.UserInfo;

import com.gy.gmall.config.RedisUtil;
import com.gy.gmall.service.UserInfoService;
import com.gy.gmall.usermanager.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    //保存三个常量用作Redis中的key
    public String userKey_prefix = "user:";
    public String userKey_suffix=".info";
    public int userKey_timeOut=60*60;

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<UserInfo> getAllUserInfo() {

        return userInfoMapper.selectAll();
    }

    //单点登录
    @Override
    public UserInfo login(UserInfo userInfo) {
        String password = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfo.setPasswd(password);
        UserInfo newUserInfo = userInfoMapper.selectOne(userInfo);
        if(newUserInfo!=null){
            //存储到redis
            Jedis jedis = redisUtil.getJedis();
            jedis.setex(userKey_prefix+newUserInfo.getId()+userKey_suffix,userKey_timeOut, JSON.toJSONString(newUserInfo));
            jedis.close();
            return newUserInfo;
        }
        return null;
    }
    //查询缓存中是否有数据
    public UserInfo verify(String userId){
        Jedis jedis = redisUtil.getJedis();
        String key = userKey_prefix+userId+userKey_suffix;
        String userJson = jedis.get(key);
        // 延长时效
        jedis.expire(key,userKey_timeOut);
        if(userJson!=null){
            UserInfo userInfo = JSON.parseObject(userJson.getBytes(), UserInfo.class);
            return userInfo;
        }
        return null;


    }
}
