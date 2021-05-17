package com.custom.service.common.rank.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.custom.config.auth.JwtToken;
import com.custom.config.exception.CommonJsonException;
import com.custom.dao.common.GameRankDao;
import com.custom.dao.common.GameRecordDao;
import com.custom.db.redis.key.Game;
import com.custom.db.redis.key.Rank;
import com.custom.db.redis.service.StringRedisService;
import com.custom.entity.common.GameRecordEntity;
import com.custom.entity.common.JwtUserEntity;
import com.custom.entity.common.PageEntity;
import com.custom.entity.common.RankEntity;
import com.custom.request.Context;
import com.custom.request.ErrorEnum;
import com.custom.request.Response;
import com.custom.service.common.rank.RankService;
import com.custom.util.JSONUtil;
import com.custom.util.PaddingUid;
import com.custom.util.PageUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RankServiceImpl implements RankService {
    @Autowired
    GameRankDao gameRankDao;

    @Autowired
    GameRecordDao gameRecordDao;

    @Autowired
    StringRedisService stringRedisService;

    @Override
    public JSONObject rank() {
        int pageOrder = Context.get().getIntValue("pageOrder");
        int pageNum = Context.get().getIntValue("pageNum");
        String gameType = Context.get().getString("gameType");
        if(pageNum == 0) pageNum = 20;
        if(pageNum > 100) pageNum = 100;
        if(pageOrder > 100) pageOrder = 100;
        PageEntity pg = PageUtil.getPage(pageOrder,pageNum);

        String keyRank = Rank.list(gameType);

        List<RankEntity> list = gameRankDao.findRankByType(gameType, pg.getOffset(), pg.getLimit());

        Map rank = PageUtil.next(list, pg);
        JSONObject body = new JSONObject();
        body.put("rank", rank);

        String authorization = Context.getHeader().getAuthorization();

        if(!StrUtil.isEmpty(authorization)){
            try{
                String splitToken = authorization.substring(5);
                Claims claims = JwtToken.getUserInfo(splitToken);
                Object obj = claims.get("data");
                JSONObject data = JSONUtil.iterateJSON(JSONUtil.toJSON(obj));
                JwtUserEntity user = new JwtUserEntity();
                BeanUtil.fillBeanWithMapIgnoreCase(data, user, false);
                int uid = PaddingUid.decrypt(user.getUid());

                Long userRank = stringRedisService.reverseRank(keyRank, String.valueOf(uid));
                userRank += 1;
                if(userRank <= 200){
                    // 说明当前用户，在Redis中的数据，是前200名；而Redis中的数据是旧的，为了保持个人排名实时性，针对这种情况进行二次查询
                    List<RankEntity> top100 = gameRankDao.findRankByTypeTop100(gameType);
                    for(int i=0;i<top100.size();i++){
                        RankEntity item = top100.get(i);
                        if(item.getUid() == uid){
                            // 说明是当前用户，将最新的排名放进来
                            userRank = Long.valueOf(i+1);
                            break;
                        }
                    }
                }
                body.put("userRank", userRank);
            }catch (Exception e){
                // 解析JWT错误，无需查询个人排名
            }
        }

        return body;
    }

    @Override
    public JSONObject commitRankScore() {
        int uid = Context.getUser().getUid();
        String token = Context.get().getString("token");
        String gameType = Context.get().getString("gameType");
        int recordId = Context.get().getIntValue("recordId");


        // TODO 这两个参数需要做加密处理，避免明文传输
        int score = Context.get().getIntValue("score");
        int success = 1;

        String startKey = Game.startGame(gameType, uid, recordId);
        String redisToken = stringRedisService.get(startKey);

        if(StrUtil.isEmpty(redisToken) || !redisToken.equals(token)){
            throw new CommonJsonException(ErrorEnum.E_GAME_TOKEN_ERROR);
        }

        int updateMaxScore = 0;
        if(success == 0){
            // 说明关卡没有通关 关卡不通关，那么不换算为魅力值，同时也不计入排行榜分数中

        }else if(success == 1){
            // 游戏通关
            String rankUserKey = Rank.userRank(gameType, uid);
            String rankExist = stringRedisService.get(rankUserKey);

            if(StrUtil.isEmpty(rankExist)){
                // 说明该用户还未加入排行榜中
                if(score > 0){
                    // 分数大于0，才应该加入排行榜单中
                    gameRankDao.addNewRankItem(uid, gameType, score, Context.getUser().getNickname(), Context.getUser().getAvatar());
                    stringRedisService.set(rankUserKey, "1");
                    updateMaxScore = 1;
                }
            }else{
                // 数据库中已经存在记录，直接调用update语句

                // 语句会自行判断，是否需要更新游戏分数
                updateMaxScore = gameRankDao.updateRankScore(uid, gameType, score);
            }
            // TODO 反作弊思考 score很容易被刷
        }


        GameRecordEntity gameRecordEntity = new GameRecordEntity();
        gameRecordEntity.setId(recordId);
        gameRecordEntity.setUid(uid);
        gameRecordEntity.setScore(score);
        gameRecordEntity.setSuccess(String.valueOf(success));
        gameRecordDao.endGame(gameRecordEntity);
        // 无论是通关 还是不通关 或者中途退出，都要调用此接口，把startKey去掉
        stringRedisService.remove(startKey);

        JSONObject body = new JSONObject();
        body.put("updateMaxScore", updateMaxScore);
        return body;
    }

    @Override
    public JSONObject startGame() {
        int uid = Context.getUser().getUid();
        String gameType = Context.get().getString("gameType");

        GameRecordEntity gameRecordEntity = new GameRecordEntity();
        gameRecordEntity.setUid(uid);
        gameRecordEntity.setType(gameType);
        gameRecordDao.add(gameRecordEntity);

        String startKey = Game.startGame(gameType, uid, gameRecordEntity.getId());
        String startToken = RandomUtil.randomString(12);

        // 增加一个开始游戏的凭证，10分钟超时
        stringRedisService.set(startKey, startToken, 60 * 10L);

        JSONObject body = new JSONObject();
        body.put("token", startToken);
        body.put("recordId", gameRecordEntity.getId());

        return body;
    }
}
