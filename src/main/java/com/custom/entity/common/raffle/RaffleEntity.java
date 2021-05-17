package com.custom.entity.common.raffle;

import lombok.Data;

@Data
public class RaffleEntity {
    private int id;
    private int activityId;
    private String raffleTimesType;
    private Integer raffleTimes = null;
    private String hitLimitType;
    private Integer hitLimitNum = null;
    private Integer useRaffleTimesLimit = null;
    private String showConfig;
    private int mustHit;
}
