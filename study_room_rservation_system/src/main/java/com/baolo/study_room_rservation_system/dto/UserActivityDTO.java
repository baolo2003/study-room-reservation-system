package com.baolo.study_room_rservation_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDTO {
    // 用户ID
    private Long userId;

    // 活动类型：SIGN_IN、RESERVE、SIGN_OUT、CANCEL、ABSENT
    private String activityType;

    private Long classroomId;
    private Long seatId;
}
