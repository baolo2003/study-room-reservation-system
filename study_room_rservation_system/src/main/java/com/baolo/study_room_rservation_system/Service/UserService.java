package com.baolo.study_room_rservation_system.Service;

import com.baolo.study_room_rservation_system.Entity.User;
import com.baolo.study_room_rservation_system.dto.UserLoginDTO;
import com.baolo.study_room_rservation_system.dto.UserRegisterDTO;
import com.baolo.study_room_rservation_system.vo.ReservationListVO;
import com.baolo.study_room_rservation_system.vo.UserVO;

import java.util.List;


public interface UserService {
    Boolean  register(UserRegisterDTO userRegisterDTO);


    UserVO login(String studentId, String password);

    void updatePassword(String studentId, String password);

    UserVO getUserInfo();

    void updateUserInfo(UserVO userVO);


   List<ReservationListVO>  getReservationlist(Integer status, Integer pageNum, Integer pageSize);
}
