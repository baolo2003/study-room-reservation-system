package com.baolo.study_room_rservation_system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserVO {
    private Long id;
    private String studentId;
    private String name;
    private String phone;
    private String role;
    private Integer activityScore;
    private Map<String, Object> token;
}
