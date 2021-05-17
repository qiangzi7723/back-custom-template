package com.custom.request;

// 通用错误码
public enum ErrorEnum {
    /*
     * 错误信息
     * */

    E_1("-1", "非法请求"),
    E_ILLEGAL_LIMIT("-1000", "活动过于火爆，请稍后再试"),

    E_400("400", "请求处理异常，请稍后再试"),
    E_500("500", "请求方式有误,请检查 GET/POST"),
    E_501("501", "请求路径不存在"),
    E_502("502", "权限不足"),
    E_503("503", "未登录"),

    E_REQUEST_LIMIT("4001","并发限制，请求失败"),


    E_AUTH_ERROR("1001","账号或者密码错误"),
    E_SYSTEM_CONCURRENCY("1002","并发限制"),
    E_GAME_TOKEN_ERROR("1013","服务器繁忙"),

    E_TODAY_LIMIT("1020","超出当日次数限制"),

    E_RAFFLE_TODAY_NO_TIMES("1040","今日抽奖已达到上限"),
    E_NO_GIFT("1041","很抱歉，活动过于火爆，奖品已经发放完毕"),
    E_RAFFLE_NO_TIMES("1042","没有抽奖机会"),
    E_NO_GIFT_CONFIG("1043","抽奖活动不存在"),
    E_NO_RAFFLE_TIMES("1044","没有抽奖机会了"),


    E_CANNT_PUBLISH("1005","状态异常，不允许发布"),
    E_NO_AUTH("1006","无权限");




    private String errorCode;

    private String errorMsg;

    ErrorEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
