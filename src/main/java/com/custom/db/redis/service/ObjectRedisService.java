package com.custom.db.redis.service;

import com.custom.db.redis.templates.JacksonRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangdebin on 2018/4/11.
 */
@Service
public class ObjectRedisService {
    /**
     * 基础方式，key和value都使用默认方式序列化
     * */
//    @Autowired
//    private RedisTemplate redisTemplate;
    /**
     * jackson方式，key是String,value使用json方式序列化
     * */
    @Autowired
    private JacksonRedisTemplate jacksonRedisTemplate;


    public Map hgetall(String key){
        Map map = jacksonRedisTemplate.opsForHash().entries(key);
        return map;
    }

    /**
     * 写入缓存 对象2Json
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            jacksonRedisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间  对象2Json
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            jacksonRedisTemplate.opsForValue().set(key,value,expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除对应的value
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set keys = jacksonRedisTemplate.keys(pattern);
        if (keys.size() > 0)
            jacksonRedisTemplate.delete(keys);
    }
    /**
     * 删除对应的value
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            jacksonRedisTemplate.delete(key);
        }
    }
    /**
     * 判断缓存中是否有对应的value
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return jacksonRedisTemplate.hasKey(key);
    }
    /**
     * 读取缓存
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result  = jacksonRedisTemplate.opsForValue().get(key);
        return result;
    }


    /**
     * 哈希 添加   Object2Json
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, String hashKey, Object value){
        jacksonRedisTemplate.opsForHash().put(key,hashKey,value);
    }


    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, String hashKey){
        return jacksonRedisTemplate.opsForHash().get(key,hashKey);
    }

    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmIncrement(String key, String hashKey){
        return jacksonRedisTemplate.opsForHash().increment(key,hashKey,1);
    }


    /**
     * 列表添加  Object2Json
     * @param k
     * @param v
     */
    public void lPush(String k,Object v){
        jacksonRedisTemplate.opsForList().rightPush(k,v);
    }


    /**
     * 列表获取
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1){
        return jacksonRedisTemplate.opsForList().range(k,l,l1);
    }


    /**
     * 集合添加  Object2Json
     * @param key
     * @param value
     */
    public void setAdd(String key,Object value){
        jacksonRedisTemplate.opsForSet().add(key,value);
    }


    /**
     * 集合获取
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key){
        return jacksonRedisTemplate.opsForSet().members(key);
    }


    /**
     * 有序集合添加
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key,Object value,double scoure){
        jacksonRedisTemplate.opsForZSet().add(key,value,scoure);
    }


    /**
     * 有序集合获取
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key,double scoure,double scoure1){
        return jacksonRedisTemplate.opsForZSet().rangeByScore(key, scoure, scoure1);
    }

}
