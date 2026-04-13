package com.baolo.study_room_rservation_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationListVO {
    private Long id;
    private LocalDateTime startTime; // 预约开始时间
    private LocalDateTime endTime;   // 预约结束时间
    private Integer status;          // 状态码
    private LocalDateTime createTime;// 创建时间
    private LocalDateTime signInTime;// 签到时间
    private LocalDateTime signOutTime;// 签退时间
    private String seatNo;// 座位号
    private String classroomName;// 教室名称
}
