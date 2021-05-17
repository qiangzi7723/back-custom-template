package com.custom.db.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class LuaRedisService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public boolean tryLock(String key, String UniqueId, int seconds) {
        DefaultRedisScript<Long> longDefaultRedisScript = new DefaultRedisScript<>("if redis.call('setnx',KEYS[1],ARGV[1]) == 1 then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end", Long.class);
        Long result = stringRedisTemplate.execute(longDefaultRedisScript, Collections.singletonList(key), UniqueId, String.valueOf(seconds));
        return result == 1;
    }
}
