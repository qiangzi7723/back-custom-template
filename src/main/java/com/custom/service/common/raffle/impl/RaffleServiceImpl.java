package com.custom.service.common.raffle.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.custom.config.exception.CommonJsonException;
import com.custom.dao.common.GiftDao;
import com.custom.dao.common.RaffleDao;
import com.custom.db.redis.key.Raffle;
import com.custom.db.redis.service.LuaRedisService;
import com.custom.db.redis.service.ObjectRedisService;
import com.custom.db.redis.service.StringRedisService;
import com.custom.db.redis.templates.JacksonRedisTemplate;
import com.custom.entity.common.raffle.AutoStockEntity;
import com.custom.entity.common.raffle.RaffleGiftEntity;
import com.custom.entity.common.raffle.RaffleConfigEntity;
import com.custom.service.common.raffle.RaffleService;
import com.custom.request.ErrorEnum;
import com.custom.request.Response;
import com.custom.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Service
public class RaffleServiceImpl implements RaffleService {
    @Autowired
    JacksonRedisTemplate redis;

    @Autowired
    ObjectRedisService redisObj;

    @Autowired
    StringRedisService stringRedisService;

    @Autowired
    LuaRedisService luaRedisService;

    @Autowired
    RaffleDao raffleDao;

    @Autowired
    GiftDao giftDao;


    @Override
    public RaffleGiftEntity raffle(String uid, int activityId) {

        // 加锁
        String redisUserLock = Raffle.lock(activityId, uid);
        boolean lock = luaRedisService.tryLock(redisUserLock,"lock",60);
        if(!lock){
            throw new CommonJsonException(ErrorEnum.E_SYSTEM_CONCURRENCY);
        }

        // 获取活动的抽奖配置 改为Redis中获取
        RaffleConfigEntity raffleConfig = getRaffleConfig(activityId);
        if(raffleConfig == null){
            // 说明抽奖ID不存在
            stringRedisService.remove(redisUserLock);
            throw new CommonJsonException(ErrorEnum.E_NO_GIFT_CONFIG);
        }

        // 获取待抽奖的奖品列表
        RaffleGiftEntity giftQuery = new RaffleGiftEntity();
        giftQuery.setActivityId(activityId);
        // 如果需要某个奖品不参与抽奖，则把奖品概率设置为0
        List<RaffleGiftEntity> giftList = giftDao.queryCanRaffle(giftQuery);

        String infoHashKey = Raffle.raffleHitGiftInfo(activityId,uid);
        Map infoMap= redisObj.hgetall(infoHashKey);

        // 查一下免费的抽奖次数
        int freeRaffleTimes = getFreeRaffleTimes(infoMap,raffleConfig);

        if(freeRaffleTimes <= 0){
            // 说明已经没有了免费次数
            stringRedisService.remove(redisUserLock);
            throw new CommonJsonException(ErrorEnum.E_NO_RAFFLE_TIMES);
//            GiftEntity gift = new GiftEntity();
//            gift.setId(-1);
//            return gift;
        }

        for (RaffleGiftEntity gift : giftList){
            if (infoMap == null || infoMap.size() == 0) break;
            for (Object key : infoMap.keySet()){
                String itemKey = (String) key;
                String[] keySplit = itemKey.split(";");
                if (!keySplit[0].equals("giftHit")) continue;
                if( Integer.valueOf(keySplit[1]) == gift.getId()){
                    // 说明用户已经中过一次该奖品了
                    gift.setHitNum(gift.getHitNum()+(Integer) infoMap.get(key));
                }
            }
        }

        boolean canHit = true;

        // 有抽奖机会，才会走到下面
        List keyList = new ArrayList<>();
        if(canHit){
            // 能中奖，才会走到这里，去取抽奖的奖品token
            for(RaffleGiftEntity gift: giftList){
                if(gift.getStockNow() > gift.getSend() || gift.getStockAll() == -1){
                    // 有库存 或者无限库存才可以加入抽奖
                    if(gift.getHitLimitNum()!=null && gift.getHitNum() >= gift.getHitLimitNum()){
                        // 说明配置了该奖品的最大中奖次数 而当前用户已经超出了 故无法再中
                        continue;
                    }
                    // 说明仍然有奖品可以抽
                    String key = Raffle.keyList(activityId, gift.getId());
                    int stockAll = gift.getStockAll();
                    // 抽奖逻辑，每一个单独的奖品，对应Redis的list，每次抽奖需要申请一个key才能抽对应奖品
                    if(stockAll != -1){
                        // -1表示奖品无库存上限
                        Object redisToken = redis.opsForList().leftPop(key);
                        if(redisToken!=null){
                            gift.setRedisToken(redisToken.toString());
                            keyList.add(gift);
                        }
                    }else{
                        gift.setRedisToken("infinite"); // 无限库存
                        keyList.add(gift);
                    }

                }
            }
        }
        // 能走到这里的，基本是锁住了redis的奖品，不会出现奖品超发
        if(raffleConfig.getMustHit() == 1 && keyList.size()==0){
            // 说明是极端情况，百分百中奖，并且奖品没有了
            // 注 这种情况下，目前是没有消耗抽奖机会的
            stringRedisService.remove(redisUserLock);
            throw new CommonJsonException(ErrorEnum.E_NO_GIFT);
        }

        RaffleGiftEntity hitGift = hitGift(keyList,raffleConfig.getMustHit());

        // 返回来的，可能是空奖品，重设一下参数
        hitGift.setActivityId(raffleConfig.getActivityId());

        if(hitGift.getId()!=-1){
            // 确实中奖了
            giftDao.increaseStock(hitGift);
            //更改为redis，缓冲数据定时扫描加入
//            String key = Raffle.raffleGift(activityId,hitGift.getId());
//            redisObj.incr(key,1);

            //使用id作为唯一标示 修改的地方
            Integer giftLogGiftId = hitGift.getId();
            redisObj.hmIncrement(infoHashKey, "giftHit"+";"+giftLogGiftId);

        }else{
            // 没有中奖 需要统一的将未中奖的Redis key还回去
        }
        refundGiftToken(keyList,hitGift);

        redisObj.hmIncrement(infoHashKey,"totalRaffle");

        // 解锁
        stringRedisService.remove(redisUserLock);
        return hitGift;
    }

    @Override
    public JSONObject autoCheck(int activityId){
        // 走一轮查询 去取raffleConfigId回来 目前该参数无作用
        RaffleConfigEntity ct = raffleDao.queryById(activityId);
        if(BeanUtil.isEmpty(ct)) return null;

        RaffleGiftEntity giftConfig = new RaffleGiftEntity();
        giftConfig.setActivityId(activityId);
        List<RaffleGiftEntity> giftList = giftDao.queryCanRaffle(giftConfig);

        for (;;) {
            boolean change = false;
            for (RaffleGiftEntity gift : giftList) {
                if (gift.getStockAll() == -1) continue;

                // 说明该奖品需要加到Redis队列中
                List list = new ArrayList<>();
                Integer todayAdd = 0;
                String key = Raffle.keyList(activityId, gift.getId());
                Long redisStockNow = redis.opsForList().size(key);
                if (redisStockNow == null) redisStockNow = 0L;

                if (redisStockNow == 0) {//没有加入过列表
                    if (gift.getStockAll() >= 20000) {
                        todayAdd = 20000;
                    } else {
                        todayAdd = gift.getStockAll();
                    }
                } else { //加入过列表
                    if ((gift.getStockAll() - redisStockNow) >= 20000) {
                        todayAdd = 20000;
                    } else {
                        todayAdd = gift.getStockAll() - redisStockNow.intValue();
                    }
                }

                for (int i = 0; i < todayAdd; i++) {
                    String keyPattern = DateUtil.today();
                    list.add(keyPattern + "-" + i);
                }
                if (list.size() > 0) {
                    change = true;
                    // 有配置奖品 才需要执行脚本
                    redis.opsForList().rightPushAll(key, list);

                    giftDao.increaseStockNow(gift, todayAdd);

                    AutoStockEntity autoStockEntity = new AutoStockEntity();
                    BeanUtils.copyProperties(gift, autoStockEntity);
                    autoStockEntity.setGiftId(gift.getId());
                    autoStockEntity.setAddStock(todayAdd);
                    raffleDao.addAutoStock(autoStockEntity);
                }

            }
            if (change == false){
                break;
            }
        }
        return Response.success("初始化成功");
    }

    // 内部方法，走到这里，代表已经经过了所有的抽奖校验，最终生成一份抽奖配置，进行概率命中
    // 只负责概率命中 能走到这里的 基本是有库存 能抽的奖品
    // 这里应该还需要传入一个参数 是否为100%中奖
    private RaffleGiftEntity hitGift(List<RaffleGiftEntity> giftList, int mustHit){

        int useProbability = 0;
        List<JSONObject> list = new ArrayList<>();
        for(RaffleGiftEntity gift: giftList){
            JSONObject randomGift = new JSONObject();
            randomGift.put("id",gift.getId());
            randomGift.put("probability",gift.getProbability()*1000); // 小数换算
            randomGift.put("start",useProbability);
            useProbability += gift.getProbability()*1000;
            randomGift.put("end",useProbability);
            list.add(randomGift);
        }

        int sumProbability = 100*1000;
        if(mustHit == 0){
            // 说明非百分百中奖，手动加上谢谢参与的情况
            int leftProbability = 100 * 1000 - useProbability; // 总的中奖概率是 100 ，*1000是小数位换算
            JSONObject emptyGift = new JSONObject();
            emptyGift.put("id",-1);
            emptyGift.put("probability",leftProbability);
            emptyGift.put("start",useProbability);
            emptyGift.put("end",leftProbability + useProbability); // 其实就是100*1000 终点
            list.add(emptyGift);
        }else{
            // 百分百中奖
            sumProbability = useProbability;

            if(sumProbability == 0){
                RaffleGiftEntity temp = new RaffleGiftEntity();
                temp.setId(-1);
                return temp;
            }
        }


        int rnd = new Random().nextInt(sumProbability);
        int hitGiftId = -1;
        for(int i=0;i<list.size();i++){
            JSONObject item = list.get(i);
            int start = item.getInteger("start");
            int end = item.getInteger("end");
            if(rnd>=start && rnd<end){
                // 说明当前奖品被抽中
                hitGiftId = item.getIntValue("id");
                break;
            }
        }


        RaffleGiftEntity temp = new RaffleGiftEntity();
        temp.setId(-1);
        giftList.add(temp);

        RaffleGiftEntity returnGift = new RaffleGiftEntity();
        returnGift.setId(-1); // 先默认设为不中奖 下面这个方法会重新设置中奖状态
        for(RaffleGiftEntity gift: giftList){
            // 不管中不中奖都会走到这里
            if(gift.getId() == hitGiftId){
                if(gift.getId() != -1){
                    // 说明是中奖了 且允许中奖
                    returnGift = gift;
                }
            }
        }
        return returnGift;
    }

    private void refundGiftToken(List<RaffleGiftEntity> giftList, RaffleGiftEntity hitGift){
        for(RaffleGiftEntity gift: giftList){
            if(gift == null || gift.getId() == hitGift.getId() || gift.getId() == -1 || gift.getRedisToken() == "infinite"){
                // 此奖品已经被抽走，无需退还token | 或者奖品是无限库存的，不需要返回
                continue;
            }
            String key = Raffle.keyList(hitGift.getActivityId(), gift.getId());
            // 返还token
            redis.opsForList().rightPush(key,gift.getRedisToken());
        }
    }

    private RaffleConfigEntity getRaffleConfig(int activityId){
        RaffleConfigEntity raffleConfig =  new RaffleConfigEntity();

        JSONObject raffleJson = (JSONObject) redisObj.get(Raffle.raffleConfig(activityId,"prod"));
        if(raffleJson!=null){
            // 说明存在数据
            BeanUtil.fillBeanWithMapIgnoreCase(raffleJson,raffleConfig,false);
        }else{
            raffleConfig.setActivityId(activityId);
            raffleConfig = raffleDao.findByActivityId(raffleConfig);
        }
        return raffleConfig;
    }

    // 查询免费的抽奖机会还剩余多少
    private int getFreeRaffleTimes(Map map,RaffleConfigEntity raffleConfig){
        int times = 0;
        if(raffleConfig.getRaffleTimesType().equals("activity")){
            times = raffleUseTimes(map,"activity");
        }else if(raffleConfig.getRaffleTimesType().equals("day")){
            times = raffleUseTimes(map,"day");
        }
        int leftTimes = raffleConfig.getRaffleTimes() - times;
        return Math.max(0,leftTimes);
    }


    private int raffleUseTimes(Map map,String type){
        if (type.equals("day")){
            Integer dayNum = (Integer)map.get("dayRaffle"+";"+ DateUtil.today());
            if (dayNum == null) return 0;
            return dayNum;
        }
        Integer dayNum = (Integer) map.get("totalRaffle");
        if (dayNum == null) return 0;
        return dayNum;
    }



}
