package com.custom.service;

import com.alibaba.fastjson.JSONObject;

public interface IndexService {
    JSONObject getStatus();

    JSONObject postStatus();

    JSONObject raffle();

    JSONObject illegalCheck();

    JSONObject illegalLevel();

    JSONObject rank();

    JSONObject commit();

    JSONObject start();
}
