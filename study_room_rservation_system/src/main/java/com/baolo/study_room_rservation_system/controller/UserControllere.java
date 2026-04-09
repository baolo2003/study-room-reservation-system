package com.baolo.study_room_rservation_system.controller;

import com.baolo.study_room_rservation_system.Entity.User;
import com.baolo.study_room_rservation_system.Service.UserService;
import com.baolo.study_room_rservation_system.Tool.Result;
import com.baolo.study_room_rservation_system.dto.UserLoginDTO;
import com.baolo.study_room_rservation_system.dto.UserRegisterDTO;
import com.baolo.study_room_rservation_system.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserControllere {

    @Autowired
    private UserService userService;
    // 用户注册
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册：{}", userRegisterDTO.getStudentId());
        boolean success = userService.register(userRegisterDTO);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.fail("学号已注册，注册失败");
        }
    }

    //用户登录
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO.getStudentId());
        UserVO  uservo = userService.login(userLoginDTO.getStudentId(), userLoginDTO.getPassword());
        if (uservo!=null) {

            return Result.success(uservo);
        } else {
            return Result.fail("用户名或密码错误，登录失败");
        }
    }
}

