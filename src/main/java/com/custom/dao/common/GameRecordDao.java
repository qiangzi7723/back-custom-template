package com.custom.dao.common;


import com.custom.entity.common.GameRecordEntity;
import org.springframework.stereotype.Component;

@Component
public interface GameRecordDao {
    void add(GameRecordEntity gameRecordEntity);
    void endGame(GameRecordEntity gameRecordEntity);
}
