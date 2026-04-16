package com.baolo.study_room_rservation_system.dto;

import lombok.Data;

@Data
public class SeatQueryDTO {
    private Long classroomId;     // 自习室区域ID
    private Integer status;        // 座位状态: 0-空闲 1-已预约 2-已占用
    private Integer isWindow;    // 靠窗: 0-否 1-是
    private Integer hasSocket;      // 有插座: 0-否 1-是
    private Integer isQuiet;      // 安静区: 0-否 1-是
}