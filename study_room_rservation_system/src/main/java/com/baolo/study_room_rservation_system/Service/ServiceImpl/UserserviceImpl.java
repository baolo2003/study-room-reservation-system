package com.baolo.study_room_rservation_system.Service.ServiceImpl;

import com.baolo.study_room_rservation_system.Entity.Classroom;
import com.baolo.study_room_rservation_system.Entity.Reservation;
import com.baolo.study_room_rservation_system.Entity.Seat;
import com.baolo.study_room_rservation_system.Entity.User;
import com.baolo.study_room_rservation_system.Mapper.ClassroomMapper;
import com.baolo.study_room_rservation_system.Mapper.ReservationMapper;
import com.baolo.study_room_rservation_system.Mapper.SeatMapper;
import com.baolo.study_room_rservation_system.Mapper.UserMapper;
import com.baolo.study_room_rservation_system.Service.UserService;
import com.baolo.study_room_rservation_system.Tool.JwtUtil;
import com.baolo.study_room_rservation_system.Tool.UserContext;
import com.baolo.study_room_rservation_system.dto.UserLoginDTO;
import com.baolo.study_room_rservation_system.dto.UserRegisterDTO;
import com.baolo.study_room_rservation_system.vo.ReservationListVO;
import com.baolo.study_room_rservation_system.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class UserserviceImpl implements UserService {


    private static final String ACCESS_TOKEN_KEY = "user:Token:access";
    private static final String REFRESH_TOKEN_KEY = "user:Token:refresh";


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ClassroomMapper classroomMapper;

    @Autowired
    private SeatMapper seatMapper;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
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
        String Accesstoken = jwtUtil.generateAccessToken(user.getId(), user.getStudentId());

        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getStudentId());

       // 保存 refreshtoken/Accesstoken 到 Redis
       stringRedisTemplate.opsForValue().set("ACCESS_TOKEN_KEY"+user.getId(), Accesstoken,10, TimeUnit.MINUTES);
       stringRedisTemplate.opsForValue().set("REFRESH_TOKEN_KEY"+user.getId(), refreshToken,7, TimeUnit.DAYS);
        // 封装 VO
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        Map<String, Object> token = new HashMap<>();
        token.put("access_token", Accesstoken);
        token.put("refresh_token", refreshToken);
        vo.setToken(token); // 把 token 给前端
        return vo;
    }

    /**
     * 修改密码
     * @param studentId
     * @param password
     */
    @Override
    public void updatePassword(String studentId, String password) {
        //先查寻用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, studentId);
        User user = userMapper.selectOne(wrapper);
        if (user == null)
        {
            throw new RuntimeException("用户不存在");
        }
        //对密码进行加密
        String md5Pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        //修改密码
        user.setPassword(md5Pwd);
        //保存到数据库
        userMapper.update(user, wrapper);
    }

    /**
     * 获取用户信息
     * @return
     */
    @Override
    public UserVO getUserInfo() {
        //获取当前用户ID
        String userId =  UserContext.getCurrentUser();
        //查询用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, userId);
        User user = userMapper.selectOne(wrapper);
        //判断用户是否存在
        if(user == null)
        {
            throw new RuntimeException("用户不存在");
        }
        //封装VO
        UserVO vo = new UserVO();
       vo.setName(user.getName());
       vo.setStudentId(user.getStudentId());
       vo.setPhone(user.getPhone());
       vo.setActivityScore(user.getActivityScore());
       vo.setId(user.getId());
        return vo;
    }

    /**
     * 修改用户信息
     * @param userVO
     */
    @Override
    public void updateUserInfo(UserVO userVO) {
        //根据用户ID查询用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, userVO.getId());
        User user = userMapper.selectOne(wrapper);
        if(user == null)
        {
            throw new RuntimeException("用户不存在");
        }
        //修改用户信息
        user.setName(userVO.getName());
        user.setPhone(userVO.getPhone());
        user.setPreference(userVO.getPreference());
        //保存到数据库
        userMapper.update(user, wrapper);
    }

    /**
     * 查询预约列表
     * @return
     */
    @Override
    public List<ReservationListVO> getReservationlist(Integer status, Integer pageNum, Integer pageSize) {
        //获取当前用户ID
        String userId = UserContext.getCurrentUser();
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, Long.parseLong(userId));
        //筛选状态
        if (status != null)
        {
            wrapper.eq(Reservation::getStatus, String.valueOf(status));
        }
        wrapper.orderByDesc(Reservation::getCreateTime);
        //分页查询
        Page<Reservation> page = new Page<>(pageNum, pageSize);
        Page<Reservation> reservationPage = reservationMapper.selectPage(page, wrapper);
        //封装VO
        return reservationPage.getRecords().stream()
                .map(reservation -> {
                    ReservationListVO vo = new ReservationListVO();
                    BeanUtils.copyProperties(reservation, vo);

                    Seat seat = seatMapper.selectById(reservation.getSeatId());
                    if (seat != null) {

                        vo.setSeatNo(seat.getSeatNo());


                        // ================== 封装 教室信息 ==================
                        Classroom classroom = classroomMapper.selectById(seat.getClassroomId());
                        if (classroom != null) {

                            vo.setClassroomName(classroom.getName());
                        }
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }




}
