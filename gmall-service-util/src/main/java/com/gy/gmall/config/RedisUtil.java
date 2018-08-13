package com.gy.gmall.config;

import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

// 取得redis的工具
// @Configuration spring3.0 xml.
public class RedisUtil {
    // 声明一个连接池
    private JedisPool jedisPool;
//    spring+jedis
//    为连接池设置一下参数

    /**
     * host,port,database 从哪里来的？
     * @param host 192.168.67.203
     * @param port 6379
     * @param database 默认的数据 0
     */
    public void initJedisPool(String host,int port,int database){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxTotal(200);
        // 获取连接时等待的最大毫秒
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        // 最少剩余数
        jedisPoolConfig.setMinIdle(10);
        // 如果到最大数，设置等待
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 等待时间
        jedisPoolConfig.setMaxWaitMillis(2000);
        // 在获取连接时，检查是否有效
        jedisPoolConfig.setTestOnBorrow(true);

        jedisPool = new JedisPool(jedisPoolConfig,host,port,20*1000);
    }

    // 取得Jedis
    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return  jedis;
    }

}
