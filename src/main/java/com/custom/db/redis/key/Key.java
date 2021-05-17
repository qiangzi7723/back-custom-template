package com.custom.db.redis.key;

import com.custom.util.Property;

public class Key {
    private static String NAMESPACE = Property.getRedisNameSpace();

    public static String common(String ...list){
        String temp = NAMESPACE;
        for(String key:list){
            temp += ":" + key;
        }
        return temp;
    }

    public static String cache(String ...list){
        String temp = NAMESPACE;
        temp += ":" + "cache";
        for(String key:list){
            temp += ":" + key;
        }
        temp += ":";
        return temp;
    }

    public static String userLock(String uid){
        return common("uid", uid, "request-lock");
    }

}
