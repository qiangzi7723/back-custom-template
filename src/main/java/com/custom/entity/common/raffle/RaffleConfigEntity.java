package com.custom.entity.common.raffle;

import lombok.Data;

@Data
public class RaffleConfigEntity {
    private int id;
    private int activityId;
    private String raffleTimesType;
    private int raffleTimes;
    private int mustHit;
}
