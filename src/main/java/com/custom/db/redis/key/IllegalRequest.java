package com.custom.db.redis.key;

public class IllegalRequest {
    public static String uid(){
        return Key.common("illegalRequest", "uid");
    }

    public static String ip(){
        return Key.common("illegalRequest", "ip");
    }
}
