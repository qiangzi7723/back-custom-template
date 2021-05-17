package com.custom.service.common.times.impl;

import com.custom.config.exception.CommonJsonException;
import com.custom.db.redis.key.TodayLimit;
import com.custom.db.redis.templates.JacksonRedisTemplate;
import com.custom.request.Context;
import com.custom.request.ErrorEnum;
import com.custom.service.common.times.TimesService;
import com.custom.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimesServiceImpl implements TimesService {
    @Autowired
    JacksonRedisTemplate redis;


    @Override
    public int isOverTodayIndex(String type, int max) {
        int uid = Context.getUser().getUid();
        String todayIndex = TodayLimit.index(uid, type, DateUtil.today());

        Integer index = (Integer) redis.opsForValue().get(todayIndex);
        if(index == null) index = 0;

        if(index >= max){
            throw new CommonJsonException(ErrorEnum.E_TODAY_LIMIT);
        }

        // 返回今日的索引 如2 表示用户今天执行该操作2次 根据type来定义
        return index;
    }

    @Override
    public int todayIndex(String type) {
        int uid = Context.getUser().getUid();
        String todayIndex = TodayLimit.index(uid, type, DateUtil.today());

        Integer index = (Integer) redis.opsForValue().get(todayIndex);
        if(index == null) index = 0;

        return index;
    }

    @Override
    public void incrTodayIndex(String type) {
        int uid = Context.getUser().getUid();
        String todayIndex = TodayLimit.index(uid, type, DateUtil.today());
        redis.opsForValue().increment(todayIndex,1L);
    }

    @Override
    public void incrTodayIndexWithValue(String type, int value) {
        int uid = Context.getUser().getUid();
        String todayIndex = TodayLimit.index(uid, type, DateUtil.today());
        redis.opsForValue().increment(todayIndex, value);
    }
}
