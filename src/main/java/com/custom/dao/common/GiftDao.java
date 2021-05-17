package com.custom.dao.common;

import com.custom.entity.common.raffle.RaffleGiftEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GiftDao {
    void increaseStockNow(RaffleGiftEntity gift, int todayAdd);
    List<RaffleGiftEntity> queryCanRaffle(RaffleGiftEntity gift);
    void increaseStock(RaffleGiftEntity gift);
}
