package com.baolo.study_room_rservation_system.controller;

import com.baolo.study_room_rservation_system.Service.UserActivityRankService;
import com.baolo.study_room_rservation_system.Tool.Result;
import com.baolo.study_room_rservation_system.dto.UserActivityDTO;
import com.baolo.study_room_rservation_system.vo.RankItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userActivity")
@Slf4j
public class UserActivityController {


    @Autowired
    private UserActivityRankService userActivityRankService;


    /**
     * 获取用户活跃度和排行
     */
    @GetMapping("/Rank")
    public Result<RankItemVo> getUserActivity(@RequestParam Long userId,
                                              @RequestParam String type) {
        log.info("获取用户活跃度");
        //调用方法进行获取
        return Result.success(userActivityRankService.getUserActivity(userId, type));
    }
    /**
     *获取排行榜
 }
     */
    @GetMapping("/rank-list")
    public Result<List<RankItemVo>> getRankList(
            @RequestParam String type,
            @RequestParam(defaultValue = "15") int topN) {
        return Result.success(userActivityRankService.getRankList(type, topN));

    }

}
