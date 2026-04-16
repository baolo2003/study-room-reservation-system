package com.baolo.study_room_rservation_system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeatDetailVO {
    private Long id;
    private Long classroomId;
    private String classroomName;
    private String seatNo;
    private Integer isWindow;
    private Integer hasSocket;
    private Integer isQuiet;
    private Integer status;
    private LocalDateTime createTime;
    private String statusText;      // 状态文本描述
    private String windowText;       // 靠窗描述
    private String socketText;      // 插座描述
    private String quietText;       // 安静区描述
}