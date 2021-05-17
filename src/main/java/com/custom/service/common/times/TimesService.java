package com.custom.service.common.times;

public interface TimesService {
    int isOverTodayIndex(String type, int max);
    int todayIndex(String type);
    void incrTodayIndex(String type);
    void incrTodayIndexWithValue(String type, int value);
}
