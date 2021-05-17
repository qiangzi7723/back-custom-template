package com.custom.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.custom.entity.common.raffle.RaffleGiftEntity;

public class CommonUtil {
    public static JSONObject simpleGift(RaffleGiftEntity gift){
        JSONObject responseGift = JSONUtil.toJSON(gift);

        String[] removeKeys = {"stockAll","stockNow","stockOnlineEveryday","send","probability","sku","isSwitch","redisToken","hitLimitNum","hitNum","value","unit","isDelete"};

        for(int i=0;i<removeKeys.length;i++){
            responseGift.remove(removeKeys[i]);
        }
        return responseGift;
    }

    public static String desensitizedPhoneNumber(String phoneNumber){
        if(!StrUtil.isEmpty(phoneNumber)){
            phoneNumber = phoneNumber.replaceAll("(\\w{3})\\w*(\\w{4})", "$1****$2");
        }
        return phoneNumber;
    }
}
