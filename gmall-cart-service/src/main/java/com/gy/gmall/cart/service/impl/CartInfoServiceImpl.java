package com.gy.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gy.gmall.bean.CartInfo;
import com.gy.gmall.bean.SkuInfo;
import com.gy.gmall.cart.constant.CartConstant;
import com.gy.gmall.cart.mapper.CartInfoMapper;
import com.gy.gmall.config.RedisUtil;
import com.gy.gmall.service.CartInfoService;
import com.gy.gmall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;

public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    CartInfoMapper cartInfoMapper;
    @Reference
    ItemService itemService;
    @Autowired
    RedisUtil redisUtil;

    //查询数据库中购物车信息
    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        CartInfo cartInfoSearch = cartInfoMapper.selectOne(cartInfo);
        if(cartInfoSearch!=null){
            //存在就更新商品数量
            cartInfoSearch.setSkuNum(skuNum+cartInfoSearch.getSkuNum());
            //更新数据库信息
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoSearch);
        }else{
            //不存在就存入
            SkuInfo skuInfo = itemService.getSkuInfo(skuId);
            CartInfo newCartInfo = new CartInfo();
            newCartInfo.setUserId(userId);
            newCartInfo.setSkuNum(skuNum);
            newCartInfo.setSkuId(skuId);
            newCartInfo.setCartPrice(skuInfo.getPrice());
            newCartInfo.setSkuName(skuInfo.getSkuName());
            newCartInfo.setSkuPrice(skuInfo.getPrice());
            newCartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfoMapper.insertSelective(newCartInfo);
            cartInfoSearch = newCartInfo;
        }
        //准备存入redis,使用hash存储
        String key = CartConstant.USER_KEY_PREFIX+userId+CartConstant.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        String cartInfoJson = JSON.toJSONString(cartInfoSearch);
        //存入
        jedis.hset(key,skuId,cartInfoJson);
        //设置失效时间
        String userInfoKey = CartConstant.USER_KEY_PREFIX+userId+CartConstant.USERINFOKEY_SUFFIX;
        Long ttl = jedis.ttl(userInfoKey);
        jedis.expire(key,ttl.intValue());
        jedis.close();

    }
    //从数据库获取购物车列表
    @Override
    public List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList==null && cartInfoList.size()==0){
            return null;
        }
        String userCartKey = CartConstant.USER_KEY_PREFIX+userId+CartConstant.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        Map<String,String> map = new HashMap<>(cartInfoList.size());
        for (CartInfo cartInfo : cartInfoList) {
            String cartJson = JSON.toJSONString(cartInfo);
            // key 都是同一个，值会产生重复覆盖！
            map.put(cartInfo.getSkuId(),cartJson);
        }
        // 将java list - redis hash
        jedis.hmset(userCartKey,map);
        jedis.close();
        return  cartInfoList;
    }
   //从redis获取购物车列表
    @Override
    public List<CartInfo> getCartList(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String key = CartConstant.USER_KEY_PREFIX+userId+CartConstant.USERINFOKEY_SUFFIX;
        List<String> cartJsons = jedis.hvals(key);
        if(cartJsons!=null&&cartJsons.size()>0){
            List<CartInfo> cartInfoList = new ArrayList<>();
            for (String cartJson : cartJsons ) {
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            //排序？
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getSkuId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        }else{
            //从数据获取
            List<CartInfo> cartInfoList = loadCartCache(userId);
            return  cartInfoList;
        }
    }
    //合并购物车
    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId) {
        List<CartInfo> cartInfoFromDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        for (CartInfo cartInfoCk : cartListFromCookie) {
            boolean isMatch = false;
            for (CartInfo cartInfoDB : cartInfoFromDB) {
                if(cartInfoDB.getSkuId().equals(cartInfoCk.getSkuId())){
                    cartInfoDB.setSkuNum(cartInfoDB.getSkuNum()+cartInfoCk.getSkuNum());
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
                    isMatch =true;
                }
            }
            if(!isMatch) {
                //数据库中没有购物车则将cookie中的数据直接插入数据库
                cartInfoCk.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfoCk);
            }
        }
        //重新在数据库查询，防止选中状态的丢失
        List<CartInfo> cartInfoList = loadCartCache(userId);
        for (CartInfo cartInfo : cartInfoList) {
            for (CartInfo ckInfo : cartListFromCookie) {
                if(cartInfo.getSkuId().equals(ckInfo.getSkuId())){
                    if ("1".equals(ckInfo.getIsChecked())){
                        cartInfo.setIsChecked(ckInfo.getIsChecked());
                        //更新redis中的isChecked
                        checkCart(cartInfo.getSkuId(),ckInfo.getIsChecked(),userId);
                    }
                }
            }
        }
        return cartInfoList;
    }
    //选中商品，改变状态
    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        /*把对应skuId的购物车的信息从redis中取出来，反序列化，修改isChecked标志。
再保存回redis中。
同时保存另一个redis的key 专门用来存储用户选中的商品，方便结算页面使用。*/
        Jedis jedis = redisUtil.getJedis();
        String key = CartConstant.USER_KEY_PREFIX+userId+CartConstant.USER_CART_KEY_SUFFIX;
        String cartJson = jedis.hget(key, skuId);
        CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
        cartInfo.setIsChecked(isChecked);
        String cartCheckedJson = JSON.toJSONString(cartInfo);
        jedis.hset(key,skuId,cartCheckedJson);
        String checkedKey = CartConstant.USER_KEY_PREFIX+userId+CartConstant.USER_CHECKED_KEY_SUFFIX;
        if("1".equals(isChecked)){
            //将选中的商品添加到Redis中
            jedis.hset(checkedKey,skuId,cartCheckedJson);
        }else{
            jedis.del(checkedKey,skuId);
        }
        jedis.close();
    }
    // 获取购物车列表
    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        //从redis中获取选中的数据
        String key = CartConstant.USER_KEY_PREFIX+userId+CartConstant.USER_CHECKED_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        List<String> cartCheckedJson = jedis.hvals(key);
        List<CartInfo> newCartInfoList = new ArrayList<>();
        for (String cartJson : cartCheckedJson) {
            CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
            newCartInfoList.add(cartInfo);
        }
        return newCartInfoList;
    }


}
