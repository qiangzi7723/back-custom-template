package com.custom.dao.common;

import com.custom.entity.common.RankEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GameRankDao {

    List<RankEntity> findRankByType(String gameType, int offset, int limit);

    List<RankEntity> findAllRankByType(String gameType);

    List<RankEntity> findRankByTypeTop100(String gameType);

    int updateRankScore(@Param("uid") int uid, @Param("gameType") String gameType, @Param("score") int score);
    void addNewRankItem(@Param("uid") int uid, @Param("gameType") String gameType, @Param("score") int score,
                        @Param("nickname") String nickname, @Param("avatar") String avatar);

    List<RankEntity> findUserRank(@Param("uid") int uid);
}
