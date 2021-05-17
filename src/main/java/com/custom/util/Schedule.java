package com.custom.util;

import com.custom.dao.common.GameRankDao;
import com.custom.db.redis.key.Rank;
import com.custom.db.redis.service.LuaRedisService;
import com.custom.db.redis.service.StringRedisService;
import com.custom.entity.common.RankEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@EnableScheduling   // 1.开启定时任务
//@EnableAsync        // 2.开启多线程
public class Schedule {
    @Autowired
    LuaRedisService luaRedisService;

    @Autowired
    StringRedisService stringRedisService;

    @Autowired
    GameRankDao gameRankDao;


    @Scheduled(fixedDelay = 1000 * 60 * 10 * 1) // 10分钟计算一次
    public void rank(){
        String key = Rank.scheduleLock();

        // 这把锁，会强制锁住5分钟
        boolean lock = luaRedisService.tryLock(key,"1",60 * 5);
        if(lock){
            System.out.println("抢到了分布式锁，开始执行排行榜刷新");
            // 取到了分布式锁，才能正常执行逻辑
            loopRank("common");

            // 逻辑执行完成，解锁
            stringRedisService.remove(key);
        }
    }

    private void loopRank(String gameType){
        List<RankEntity> list = gameRankDao.findAllRankByType(gameType);

        String keyRank = Rank.list(gameType);
        for(int i=0;i<list.size();i++){
            // TODO 待优化 遍历速度很慢
            RankEntity entity = list.get(i);
            stringRedisService.zAdd(keyRank, String.valueOf(entity.getUid()), entity.getScore());
        }
    }
}

