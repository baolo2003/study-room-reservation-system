package com.baolo.study_room_rservation_system.Service;

import com.baolo.study_room_rservation_system.Entity.User;
import com.baolo.study_room_rservation_system.Mapper.UserMapper;
import com.baolo.study_room_rservation_system.Tool.JwtUtil;
import com.baolo.study_room_rservation_system.dto.UserLoginDTO;
import com.baolo.study_room_rservation_system.dto.UserRegisterDTO;
import com.baolo.study_room_rservation_system.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserserviceImpl implements UserService{


    @Autowired
    private UserMapper userMapper;


    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public Boolean register(UserRegisterDTO userRegisterDTO) {

       //先查看是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, userRegisterDTO.getStudentId());
        User user = userMapper.selectOne(wrapper);

        if (( user!= null))
         {
             return false;
         }
         //没有注册的用户，进行密码加密

        String password = userRegisterDTO.getPassword();
        String md5password = DigestUtils.md5DigestAsHex(password.getBytes());
       //封装数据
        User newuser = new User();
        newuser.setName(userRegisterDTO.getName());
        newuser.setPassword(md5password);
        newuser.setStudentId(userRegisterDTO.getStudentId());
        newuser.setPhone(userRegisterDTO.getPhone());
        newuser.setRole("user");
        newuser.setStatus(1);
        newuser.setActivityScore(0);
        newuser.setCreateTime(LocalDateTime.now());
        //保持到数据库
        userMapper.insert(newuser);
        return true;
    }

    @Override
    public UserVO login(String studentId, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, studentId);
        User user = userMapper.selectOne(wrapper);
        if (user == null)
        {
            throw new RuntimeException("用户不存在");
        }
        String md5Pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Pwd.equals(user.getPassword()))
        {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0)
        {
            throw new RuntimeException("账号已禁用");
        }
        // ========== 生成 JWT ==========
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        String token = jwtUtil.generateToken(claims, user.getId().toString());

        // 封装 VO
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setToken(token); // 把 token 给前端

        return vo;
    }

}
