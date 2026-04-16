package com.baolo.study_room_rservation_system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatVO {
    private Long id;
    private Long classroomId;
    private String classroomName;
    private String seatNo;
    private Integer isWindow;      // 0-否 1-是
    private Integer hasSocket;   // 0-否 1-是
    private Integer isQuiet;     // 0-否 1-是
    private Integer status;     // 0-空闲 1-已预约 2-已占用(使用中)
}