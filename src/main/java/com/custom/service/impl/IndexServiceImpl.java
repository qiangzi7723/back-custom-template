package com.custom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.custom.config.exception.CommonJsonException;
import com.custom.entity.common.raffle.RaffleGiftEntity;
import com.custom.entity.common.JwtUserEntity;
import com.custom.service.common.illegalRequest.IllegalRequestService;
import com.custom.service.common.raffle.impl.RaffleServiceImpl;
import com.custom.request.Context;
import com.custom.request.ErrorEnum;
import com.custom.request.Response;
import com.custom.service.IndexService;
import com.custom.service.common.rank.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    RaffleServiceImpl raffleService;

    @Autowired
    IllegalRequestService illegalRequestService;

    @Autowired
    RankService rankService;

    @Override
    public JSONObject getStatus() {
        return Response.success();
    }

    @Override
    public JSONObject postStatus() {
        return Response.success();
    }

    @Override
    public JSONObject raffle() {
        JwtUserEntity user = Context.getUser();
        int uid = user.getUid();

        int activityId = Context.get().getIntValue("activityId");

        if(!Context.getHeader().getReferer().contains("swagger")){
            // 说明请求不来源于 swagger 此为调试用接口，仅支持swagger访问
            throw new CommonJsonException(ErrorEnum.E_1);
        }

        RaffleGiftEntity raffleGiftEntity = raffleService.raffle(String.valueOf(uid), activityId);
        return Response.success(raffleGiftEntity);
    }

    @Override
    public JSONObject illegalCheck() {
        illegalRequestService.limitIllegal(2);
        // 如果是黑名单，无法走到下面操作。应用场景：可以在某些不希望黑名单用户访问的核心接口中，如提交分数前加入此判断
        return Response.success();
        // 黑名单统计逻辑：代码内会加入后端的限制，用户每触发一次非法限制，就会被拉黑一次。如果跟前端做好前端的校验，那么只有脚本用户，才有可能触发拉黑统计
    }

    @Override
    public JSONObject illegalLevel() {
        // 返回是否为黑名单 应用场景：抽奖时，如果是黑名单，可以永远都不给中奖。这样黑名单用户既不清楚自己被拉黑了，也无法中奖
        return Response.success(illegalRequestService.isIllegal(2));
    }

    @Override
    public JSONObject rank() {
        JSONObject body = rankService.rank();
        return Response.success(body);
    }

    @Override
    public JSONObject commit() {
        JSONObject body = rankService.commitRankScore();
        return Response.success(body);
    }

    @Override
    public JSONObject start() {
        JSONObject body = rankService.startGame();
        return Response.success(body);
    }
}
