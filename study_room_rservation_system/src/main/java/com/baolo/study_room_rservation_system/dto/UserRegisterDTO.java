package com.baolo.study_room_rservation_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterDTO {
    private String studentId;   // 学号
    private String name;        // 姓名
    private String password;    // 密码
    private String phone;       // 手机号
}
