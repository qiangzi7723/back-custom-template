package com.custom.entity.common.raffle;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

// 大部分字段 对应 raffle_config_gift这个表
@Data
@Component
public class RaffleGiftEntity {
    private int id;
    private int activityId;
    private int stockAll;
    private int stockNow;
    private int stockOnlineEveryday;
    private int send;
    private double probability;
    private String name;
    private String redisToken;
    private Integer hitLimitNum = null;

    private String description;
    private int hitNum; // 当前用户中了多少次该奖品 特殊场景下使用
    private String img;
}
