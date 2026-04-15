package com.baolo.study_room_rservation_system.vo;

import lombok.Data;

@Data
public class RankItemVo {
    /**
     * 排名
     */
    private Integer rank;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 用户id
     */
    private Long id ;
    /**
     * 用户名
     */
    private String name;
}
