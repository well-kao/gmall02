package com.gy.gmall.passport.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

public class JwtUtil {
    //编码
    public static String encode(String key, Map<String,Object> param, String salt){
        if(salt!=null){
            key +=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256,key);
        jwtBuilder=jwtBuilder.setClaims(param);
        String token = jwtBuilder.compact();
        return token;
    }
    //解码
    public static Map<String,Object> decode(String token,String salt,String key){
        Claims claims = null;
        if(salt!=null){
            key += salt;
        }
        claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return claims;
    }

}
