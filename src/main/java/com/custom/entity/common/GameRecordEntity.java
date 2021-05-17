package com.custom.entity.common;

import lombok.Data;

@Data
public class GameRecordEntity {
    private int id;
    private int uid;
    private String type;
    private int gameLevel;
    private String success;
    private int score;
}

