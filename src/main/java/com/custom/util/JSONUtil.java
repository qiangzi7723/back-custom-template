package com.custom.util;

import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
    public static JSONObject toJSON(Object entity){
        return JSONObject.parseObject(JSONObject.toJSONString(entity));
    }

    public static JSONObject iterateJSON(JSONObject object){
        for(String key: object.keySet()){
            try {
                JSONObject content = object.getJSONObject(key);
                object.put(key,iterateJSON(content));
            }catch (Exception e){

            }
        }
        return object;
    }
}

