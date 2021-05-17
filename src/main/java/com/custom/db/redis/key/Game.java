package com.custom.db.redis.key;

public class Game {
    public static String startGame(String gameType, int uid, int recordId){
        return Key.common("game", "start", "gameType", gameType, "uid", String.valueOf(uid), "recordId", String.valueOf(recordId));
    }
}
