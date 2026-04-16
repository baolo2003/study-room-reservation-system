package com.baolo.study_room_rservation_system.Service;

import com.baolo.study_room_rservation_system.dto.UserActivityDTO;
import com.baolo.study_room_rservation_system.vo.RankItemVo;

import java.util.List;

public interface UserActivityRankService {
    void updateUserActivity(UserActivityDTO userActivityDTO);

    RankItemVo getUserActivity(Long userId, String type);

    List<RankItemVo> getRankList(String type, int topN);
}
