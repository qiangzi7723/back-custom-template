package com.custom.db.redis.key;

public class Raffle {
    private static final String CONFIG = "config";
    private static final String ACTIVITYID = "activityId";

    public static String lock(int activityId, String uid){
        return Key.common("lock", "raffle", "activityId", String.valueOf(activityId), "uid", uid);
    }

    public static String raffleConfig(int activityId, String env){
        return Key.common(ACTIVITYID,String.valueOf(activityId),"env",env,CONFIG,"raffleConfig");
    }

    public static String raffleHitGiftInfo(int activityId,String uid){
        return Key.common(ACTIVITYID,String.valueOf(activityId),"raffleHitGiftInfoHash","uid",uid);
    }

    public static String raffleGift(int activityId,int raffleConfigId){
        return Key.common(ACTIVITYID,String.valueOf(activityId),"raffleConfigGift",String.valueOf(raffleConfigId));
    }

    public static String number(int activityId){
        return Key.common(ACTIVITYID,String.valueOf(activityId),"raffle-uv");
    }


    public static String keyList(int activityId, int giftId){
        return Key.common("raffleToken","activityId",String.valueOf(activityId),"giftId",String.valueOf(giftId));
    }
}
