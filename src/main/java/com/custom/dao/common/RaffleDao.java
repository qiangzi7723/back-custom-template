package com.custom.dao.common;

import com.custom.entity.common.raffle.AutoStockEntity;
import com.custom.entity.common.raffle.RaffleConfigEntity;
import org.springframework.stereotype.Component;

@Component
public interface RaffleDao {
    RaffleConfigEntity findByActivityId(RaffleConfigEntity query);
    RaffleConfigEntity queryById(int activityId);
    void addAutoStock(AutoStockEntity autoStockEntity);
}
