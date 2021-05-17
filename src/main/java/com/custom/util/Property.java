package com.custom.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Property {
    private static String jwtKey;
    @Value("${jwt.secret}")
    public void setJwtKey(String properties){
        Property.jwtKey = properties;
    }
    public static String getJwtKey(){
        return jwtKey;
    }

    private static String redisNameSpace;
    @Value("${redisNameSpace}")
    public void setRedisNameSpace(String redisNameSpace){
        Property.redisNameSpace = redisNameSpace;
    }
    public static String getRedisNameSpace(){
        return redisNameSpace;
    }

    private static String appid;
    @Value("${wx.miniapp.configs[0].appid}")
    public void setAppid(String appid){
        Property.appid = appid;
    }
    public static String getAppid(){
        return appid;
    }
}
