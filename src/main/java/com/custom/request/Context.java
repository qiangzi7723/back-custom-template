package com.custom.request;


import com.alibaba.fastjson.JSONObject;
import com.custom.entity.common.HeaderEntity;
import com.custom.entity.common.JwtUserEntity;
import sun.jvm.hotspot.memory.HeapBlock;


public class Context {

    private final static ThreadLocal<JSONObject> holder = new ThreadLocal<>();

    public static void remove() {
        holder.remove();
    }

    public static void set(String key, JSONObject value) {
        getContext().put(key, value);
    }


    public static JSONObject get(String key) {
        return getContext().getJSONObject(key);
    }

    static private JSONObject getContext() {
        JSONObject map = holder.get();
        if (map == null) {
            map = new JSONObject();
            holder.set(map);
        }
        return map;
    }

    public static JSONObject get() {
        return getContext().getJSONObject("requestParams");
    }

    public static void setUser(JwtUserEntity user) {
        getContext().put("user", user);
    }

    public static JwtUserEntity getUser() {
        return (JwtUserEntity) getContext().get("user");
    }

    public static void setKey(String key, boolean flag){
        getContext().put(key, flag);
    }

    public static boolean getKey(String key){
        return getContext().getBoolean(key);
    }

    public static void setHeader(HeaderEntity header){
        getContext().put("header", header);
    }

    public static HeaderEntity getHeader() {
        return (HeaderEntity) getContext().get("header");
    }

}

