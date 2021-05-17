package com.custom.service.common.rank;

import com.alibaba.fastjson.JSONObject;

public interface RankService {
    JSONObject startGame();
    JSONObject commitRankScore();
    JSONObject rank();
}
