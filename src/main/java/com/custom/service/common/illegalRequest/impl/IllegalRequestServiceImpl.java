package com.custom.service.common.illegalRequest.impl;

import com.custom.config.exception.CommonJsonException;
import com.custom.db.redis.key.IllegalRequest;
import com.custom.db.redis.templates.JacksonRedisTemplate;
import com.custom.request.Context;
import com.custom.request.ErrorEnum;
import com.custom.service.common.illegalRequest.IllegalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IllegalRequestServiceImpl implements IllegalRequestService {
    @Value("${illegal.request.log}")
    private boolean illegalRequestLog;

    @Autowired
    private JacksonRedisTemplate jacksonRedisTemplate;

    @Override
    public void limitIllegal(int limit) {
        // 没有开启白名单功能，直接通过
        if(!illegalRequestLog) return;
        if(isIllegalUser(limit)){
            // 说明查询表示，此为非法用户
            throw new CommonJsonException(ErrorEnum.E_ILLEGAL_LIMIT);
        }
    }

    @Override
    public boolean isIllegal(int limit) {
        if(!illegalRequestLog) return false;
        return isIllegalUser(limit);
    }

    private boolean isIllegalUser(int limit){
        // 核心的接口，需要加上这个校验。只有非法请求数量，少于参数数值，才可以通过
        String uidKey = IllegalRequest.uid();
        String ipKey = IllegalRequest.ip();

        int uid = Context.getUser().getUid();
        String ip = Context.getHeader().getIp();

        double uidTimes = jacksonRedisTemplate.opsForZSet().score(uidKey, uid);
        double ipTimes = jacksonRedisTemplate.opsForZSet().score(ipKey, ip);

        if(uidTimes >= limit || ipTimes >= limit){
            // 说明已经超出限制了
            return true;
        }

        return false;
    }
}
