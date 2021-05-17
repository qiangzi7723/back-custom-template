package com.custom.db.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangdebin on 2018/4/11.
 */
@Service
public class StringRedisService {
    /**
     * String方式，key和value都是使用String.
     * */
    @Autowired
    private StringRedisTemplate   stringRedisTemplate;

    public Long reverseRank(String key, Object object){
        return stringRedisTemplate.opsForZSet().reverseRank(key, object);
    }


    /**
     * 写入缓存  String
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间   String
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value, Long expireTime) {
        boolean result = false;
        try {
            stringRedisTemplate.opsForValue().set(key,value,expireTime, TimeUnit.SECONDS);
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
        Set keys = stringRedisTemplate.keys(pattern);
        if (keys.size() > 0)
            stringRedisTemplate.delete(keys);
    }
    /**
     * 删除对应的value
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            stringRedisTemplate.delete(key);
        }
    }
    /**
     * 判断缓存中是否有对应的value
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return stringRedisTemplate.hasKey(key);
    }


    /**
     * 读取缓存
     * @param key
     * @return
     */
    public String get(final String key) {
        String result  = stringRedisTemplate.opsForValue().get(key);
        return result;
    }

    /**
     * 哈希 添加  String
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, String hashKey, String value){
        stringRedisTemplate.opsForHash().put(key,hashKey,value);
    }

    /**
     * 哈希 增长值
     * @param key
     * @param hashKey
     * @param value
     */
    public Long hmIncrement(String key, String hashKey, Long value){
        return stringRedisTemplate.opsForHash().increment(key,hashKey,value);
    }

    /**
     * 哈希 增长值
     * @param key
     * @param hashKey
     */
    public Long hmDel(String key, String hashKey){
        return stringRedisTemplate.opsForHash().delete(key,hashKey);
    }

    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, String hashKey){
        return stringRedisTemplate.opsForHash().get(key,hashKey);
    }


    /**
     * 列表添加  String
     * @param k
     * @param v
     */
    public void lPush(String k,String v){
        stringRedisTemplate.opsForList().rightPush(k,v);
    }

    /**
     * 列表获取
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<String> lRange(String k, long l, long l1){
        return stringRedisTemplate.opsForList().range(k,l,l1);
    }

    /**
     * 集合添加  String
     * @param key
     * @param value
     */
    public void setAdd(String key,String value){
        stringRedisTemplate.opsForSet().add(key,value);
    }


    /**
     * 集合获取
     * @param key
     * @return
     */
    public Set<String> setMembersString(String key){
        return stringRedisTemplate.opsForSet().members(key);
    }


    /**
     * 有序集合添加
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key,String value,double scoure){
        stringRedisTemplate.opsForZSet().add(key,value,scoure);
    }



    /**
     * 有序集合获取
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<String> rangeByScoreString(String key,double scoure,double scoure1){
        return stringRedisTemplate.opsForZSet().rangeByScore(key, scoure, scoure1);
    }
}
