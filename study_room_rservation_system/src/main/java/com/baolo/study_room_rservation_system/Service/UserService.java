package com.baolo.study_room_rservation_system.Service;

import com.baolo.study_room_rservation_system.Entity.User;
import com.baolo.study_room_rservation_system.dto.UserLoginDTO;
import com.baolo.study_room_rservation_system.dto.UserRegisterDTO;
import com.baolo.study_room_rservation_system.vo.UserVO;


public interface UserService {
    Boolean  register(UserRegisterDTO userRegisterDTO);


    UserVO login(String studentId, String password);
}
