package com.custom.db.redis.key;

public class Rank {
    public static String scheduleLock(){
        return Key.common("rank", "schedule");
    }

    public static String list(String gameType){
        return Key.common("rank", "list", "gameType", gameType);
    }

    public static String userRank(String gameType, int uid){
        return Key.common("rank", "gameType", gameType, "uid", String.valueOf(uid));
    }
}
