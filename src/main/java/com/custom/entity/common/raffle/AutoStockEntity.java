package com.custom.entity.common.raffle;

import lombok.Data;

@Data
public class AutoStockEntity {
    private int id;
    private int activityId;
    private int raffleConfigId;
    private int giftId;
    private int addStock;
}
