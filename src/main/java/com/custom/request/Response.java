package com.custom.request;

import com.alibaba.fastjson.JSONObject;

public class Response {
    /**
     * 返回一个返回码为0的json
     */
    public static JSONObject success(Object data) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("code", Code.SUCCESS_CODE);
        resultJson.put("msg", Code.SUCCESS_MSG);
        resultJson.put("data", data);
        return resultJson;
    }

    public static JSONObject success() {
        JSONObject resultJson = new JSONObject();
        resultJson.put("code", Code.SUCCESS_CODE);
        resultJson.put("msg", Code.SUCCESS_MSG);
        resultJson.put("data", new JSONObject());
        return resultJson;
    }

    /**
     * 返回错误信息JSON
     */
    public static JSONObject error(ErrorEnum errorEnum) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("code", errorEnum.getErrorCode());
        resultJson.put("msg", errorEnum.getErrorMsg());
        return resultJson;
    }

}
