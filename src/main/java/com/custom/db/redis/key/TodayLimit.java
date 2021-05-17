package com.custom.db.redis.key;

public class TodayLimit {
    public static String index(int uid,  String type, String date){
        return Key.common("todayLimit", "uid", String.valueOf(uid), "type", type ,"date", date);
    }
}
