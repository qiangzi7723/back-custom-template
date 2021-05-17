package com.custom.service.common.raffle;

import com.alibaba.fastjson.JSONObject;
import com.custom.entity.common.raffle.RaffleGiftEntity;

public interface RaffleService {
    RaffleGiftEntity raffle(String uid, int activityId);
    JSONObject autoCheck(int activityId);
}
