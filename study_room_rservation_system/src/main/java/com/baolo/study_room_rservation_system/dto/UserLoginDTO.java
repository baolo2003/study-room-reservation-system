package com.baolo.study_room_rservation_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginDTO {
    private String studentId;
    private String password;
}
