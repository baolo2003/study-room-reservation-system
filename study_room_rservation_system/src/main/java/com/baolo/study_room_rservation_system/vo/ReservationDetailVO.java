package com.baolo.study_room_rservation_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDetailVO {
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signInTime;  // 签到时间
    private LocalDateTime signOutTime; // 签退时间
    private Integer status;
    private LocalDateTime createTime;
}
