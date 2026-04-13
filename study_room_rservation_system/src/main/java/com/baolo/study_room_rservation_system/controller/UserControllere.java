package com.baolo.study_room_rservation_system.controller;

import com.baolo.study_room_rservation_system.Entity.User;
import com.baolo.study_room_rservation_system.Service.UserService;
import com.baolo.study_room_rservation_system.Tool.JwtUtil;
import com.baolo.study_room_rservation_system.Tool.Result;
import com.baolo.study_room_rservation_system.Tool.UserContext;
import com.baolo.study_room_rservation_system.dto.UserLoginDTO;
import com.baolo.study_room_rservation_system.dto.UserRegisterDTO;
import com.baolo.study_room_rservation_system.vo.ReservationListVO;
import com.baolo.study_room_rservation_system.vo.UserVO;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserControllere {

    @Autowired
    private UserService userService;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 用户注册
     *
     */
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

    /**
     * 用户登录
     *
     */
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

    /**
     * 用户登出
     *
     */
    @PostMapping("/logout")
    public Result logout() {

        //获取当前用户ID
        String userId = UserContext.getCurrentUser();
        //将redis中的令牌删除
        redisTemplate.delete("ACCESS_TOKEN_KEY"+userId);
        redisTemplate.delete("REFRESH_TOKEN_KEY"+userId);
        return Result.success("登出成功");
    }



    /**
     * token无感刷新
     */
    @PostMapping("refreshtoken")
    public Result refresh(@RequestParam String RefreshToken) {
        // 1. 校验 Refresh Token 是否合法
        boolean token = jwtUtil.validateToken(RefreshToken);
       if(!token)
       {
           return Result.fail("Refresh Token无效，请重新登录");
       }
        // 2. 解析Reshefresh Token
        Claims claims = jwtUtil.parseToken(RefreshToken);

         //3. 获取用户ID
        Long userId = jwtUtil.getUserId(claims);
        String studentId = jwtUtil.getStudentId(claims);

        // 4. 生成新 Access Token（Refresh Token 不换！）
        String newAccessToken = jwtUtil.generateAccessToken(userId, studentId);
        // 5. 只返回新的 AccessToken
        return Result.success(newAccessToken);
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatepassword(@RequestParam String studentId, @RequestParam String password)
    {

        log.info("修改密码：{}", studentId);
        userService.updatePassword(studentId, password);
        return Result.success("修改密码成功");
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/Info")
    public Result<UserVO> getUserInfo()
    {
            log.info("获取用户信息");
            return Result.success(userService.getUserInfo());
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/Info")
    public Result updateUserInfo(@RequestBody UserVO userVO)
    {
       log.info("修改用户信息：{}",userVO.getId() );
       userService.updateUserInfo(userVO);
       return Result.success("修改成功");
    }
    /**
     * 查询用户预约记录
     */
    @GetMapping("/reservation")
    public Result<List<ReservationListVO>> getReservation(@RequestParam Integer status,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize)
    {
       log.info("查询用户预约记录");
       return Result.success(userService.getReservationlist(status, pageNum, pageSize));
    }

}

