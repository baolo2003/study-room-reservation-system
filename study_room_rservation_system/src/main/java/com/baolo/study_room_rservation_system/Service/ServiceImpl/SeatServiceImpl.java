package com.baolo.study_room_rservation_system.Service.ServiceImpl;

import com.baolo.study_room_rservation_system.Entity.Classroom;
import com.baolo.study_room_rservation_system.Entity.Reservation;
import com.baolo.study_room_rservation_system.Entity.Seat;

import com.baolo.study_room_rservation_system.Entity.User;
import com.baolo.study_room_rservation_system.Enum.ActivityTypeEnum;
import com.baolo.study_room_rservation_system.Exception.CustomizeException;
import com.baolo.study_room_rservation_system.Mapper.ClassroomMapper;
import com.baolo.study_room_rservation_system.Mapper.ReservationMapper;
import com.baolo.study_room_rservation_system.Mapper.SeatMapper;
import com.baolo.study_room_rservation_system.Mapper.UserMapper;
import com.baolo.study_room_rservation_system.Service.SeatService;
import com.baolo.study_room_rservation_system.Service.UserActivityRankService;
import com.baolo.study_room_rservation_system.Tool.UserContext;
import com.baolo.study_room_rservation_system.dto.ReservationDTO;
import com.baolo.study_room_rservation_system.dto.SeatQueryDTO;
import com.baolo.study_room_rservation_system.dto.UserActivityDTO;
import com.baolo.study_room_rservation_system.vo.SeatDetailVO;
import com.baolo.study_room_rservation_system.vo.SeatVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ClassroomMapper classroomMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserActivityRankService userActivityRankService;
    /**
     * 座位查询支持条件查询
     * @param queryDTO
     * @return
     */
    @Override
    public List<SeatVO> querySeats(SeatQueryDTO queryDTO) {
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getClassroomId() != null) {
            wrapper.eq(Seat::getClassroomId, queryDTO.getClassroomId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Seat::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getIsWindow() != null) {
            wrapper.eq(Seat::getIsWindow, queryDTO.getIsWindow());
        }
        if (queryDTO.getHasSocket() != null) {
            wrapper.eq(Seat::getHasSocket, queryDTO.getHasSocket());
        }
        if (queryDTO.getIsQuiet() != null) {
            wrapper.eq(Seat::getIsQuiet, queryDTO.getIsQuiet());
        }

        wrapper.orderByAsc(Seat::getClassroomId).orderByAsc(Seat::getSeatNo);

        List<Seat> seats = seatMapper.selectList(wrapper);


        return seats.stream().map(seat -> {
            //根据自习室ID查询自习室名称
            Classroom classroom = classroomMapper.selectById(seat.getClassroomId());
            SeatVO vo = new SeatVO();
            vo.setId(seat.getId());
            vo.setClassroomId(seat.getClassroomId());
            vo.setClassroomName(classroom.getName());
            vo.setSeatNo(seat.getSeatNo());
            vo.setIsWindow(seat.getIsWindow());
            vo.setHasSocket(seat.getHasSocket());
            vo.setIsQuiet(seat.getIsQuiet());
            vo.setStatus(seat.getStatus());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据座位id获取座位详情
     * @param seatId
     * @return
     */
    @Override
    public SeatDetailVO getSeatDetail(Long seatId) {
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null) {
            throw new CustomizeException(404, "座位不存在");
        }

        Classroom classroom = classroomMapper.selectById(seat.getClassroomId());

        SeatDetailVO vo = new SeatDetailVO();
        vo.setId(seat.getId());
        vo.setClassroomId(seat.getClassroomId());
        vo.setClassroomName(classroom != null ? classroom.getName() : null);
        vo.setSeatNo(seat.getSeatNo());
        vo.setIsWindow(seat.getIsWindow());
        vo.setHasSocket(seat.getHasSocket());
        vo.setIsQuiet(seat.getIsQuiet());
        vo.setStatus(seat.getStatus());
        vo.setCreateTime(seat.getCreateTime());

        // 状态文本
        switch (seat.getStatus())
        {
            case 0:
                vo.setStatusText("空闲");
                break;
            case 1:
                vo.setStatusText("已预约");
                break;
            case 2:
                vo.setStatusText("已占用");
                break;
        }
        vo.setWindowText(seat.getIsWindow() == 1 ? "靠窗" : "非靠窗");
        vo.setSocketText(seat.getHasSocket() == 1 ? "有插座" : "无插座");
        vo.setQuietText(seat.getIsQuiet() == 1 ? "安静区" : "普通区");

        return vo;
    }

    /**
     * 获取座位分布图
      * @param classroomId
     * @return
     */
    @Override
    public List<SeatVO> getSeatMap(Long classroomId) {
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        if (classroomId != null) {
            wrapper.eq(Seat::getClassroomId, classroomId);
        }
        wrapper.orderByAsc(Seat::getSeatNo);

        List<Seat> seats = seatMapper.selectList(wrapper);



        return seats.stream().map(seat -> {
            //根据自习室ID查询自习室名称
            Classroom classroom = classroomMapper.selectById(seat.getClassroomId());
            SeatVO vo = new SeatVO();
            vo.setId(seat.getId());
            vo.setClassroomId(seat.getClassroomId());
            vo.setClassroomName(classroom.getName());
            vo.setSeatNo(seat.getSeatNo());
            vo.setIsWindow(seat.getIsWindow());
            vo.setHasSocket(seat.getHasSocket());
            vo.setIsQuiet(seat.getIsQuiet());
            vo.setStatus(seat.getStatus());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 座位预约
     * @param reservationDTO
     */
    @Override
    @Transactional
    public void reserveSeat(ReservationDTO reservationDTO) {
        // 判断座位ID不能为空
        if (reservationDTO.getSeatId() == null) {
            throw new CustomizeException(400, "座位ID不能为空");
        }
        // 判断预约时间不能为空
        if (reservationDTO.getStartTime() == null || reservationDTO.getEndTime() == null) {
            throw new CustomizeException(400, "预约时间不能为空");
        }

        LocalDateTime startTime = reservationDTO.getStartTime();
        LocalDateTime endTime = reservationDTO.getEndTime();

        // 校验预约时间不能早于当前教室的营业时间段
        Classroom classroom = classroomMapper.selectById(reservationDTO.getClassroomId());
        String Time = classroom.getOpenTime();
        //获取开营业时间
        String[] split = Time.split("-");
        String opentime = split[0];
        String closetime = split[1];
        LocalDateTime openTime = LocalDateTime.parse(opentime);
        LocalDateTime closeTime = LocalDateTime.parse(closetime);
        if (startTime.isBefore(openTime) || endTime.isAfter(closeTime)) {
            throw new CustomizeException(400, "预约时间不在营业时间内");
        }

        // 校验最长预约时长不超过4小时
        Duration duration = Duration.between(startTime, endTime);
        if (duration.toHours() > 4) {
            throw new CustomizeException(400, "最长预约时长不超过4小时");
        }

        // 获取当前用户
        String userIdStr = UserContext.getCurrentUser();
        Long userId = Long.parseLong(userIdStr);

        // 查询座位
        Seat seat = seatMapper.selectById(reservationDTO.getSeatId());
        if (seat == null) {
            throw new CustomizeException(404, "座位不存在");
        }

        // 校验座位状态（只有空闲座位可预约）
        if (seat.getStatus() != 0) {
            throw new CustomizeException(400, "该座位当前不可预约");
        }

        // 检查该时间段是否已有预约冲突
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getSeatId, reservationDTO.getSeatId())
                .eq(Reservation::getStatus, 1)  // 已预约状态
                .le(Reservation::getStartTime, endTime)
                .ge(Reservation::getEndTime, startTime);

        Long count = reservationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new CustomizeException(400, "该时间段已被预约");
        }

        // 创建预约记录
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSeatId(reservationDTO.getSeatId());
        reservation.setClassroomId(seat.getClassroomId());
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus(1);  // 已预约
        reservation.setCreateTime(LocalDateTime.now());

        reservationMapper.insert(reservation);

        // 更新座位状态为已预约
        seat.setStatus(1);
        seatMapper.updateById(seat);

        try {
            //更新用户活跃度
            UserActivityDTO userActivityDTO = new UserActivityDTO(userId, "RESERVE", reservationDTO.getClassroomId(), reservationDTO.getSeatId());
            userActivityRankService.updateUserActivity(userActivityDTO);
        }catch (Exception e)
            {
                throw new CustomizeException(500, "活跃度更新失败");
            }
        // TODO: 发送预约提醒（可以接入消息队列或推送服务）
        // 这里暂不实现，可通过日志模拟
        System.out.println("预约提醒：座位" + seat.getSeatNo() + "，预约时间：" + startTime + "至" + endTime + "，请在开始后15分钟内签到");
    }

    /**
     * 取消预约
     * @param reservationId
     */

    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {
        String userIdStr = UserContext.getCurrentUser();
        Long userId = Long.parseLong(userIdStr);

        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new CustomizeException(404, "预约记录不存在");
        }

        // 校验是否是本人预约
        if (!reservation.getUserId().equals(userId)) {
            throw new CustomizeException(403, "只能取消自己的预约");
        }

        // 校验预约状态
        if (reservation.getStatus() != 1) {
            throw new CustomizeException(400, "只有已预约状态可取消");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = reservation.getStartTime();

        // 距预约开始时间不足30分钟，禁止取消（视为违约）
        if (Duration.between(now, startTime).toMinutes() < 30) {
            throw new CustomizeException(400, "需在预约开始前30分钟取消，否则视为违约");
        }

        // 更新预约状态为已取消
        reservation.setStatus(3);  // 3-已取消
        reservationMapper.updateById(reservation);

        // 恢复座位状态为空闲
        Seat seat = seatMapper.selectById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus(0);
            seatMapper.updateById(seat);
        }

        try
            {
                //更新用户活跃度
                UserActivityDTO userActivityDTO = new UserActivityDTO(userId, "CANCEL", reservation.getClassroomId(), reservation.getSeatId());
                userActivityRankService.updateUserActivity(userActivityDTO);
            } catch (Exception e) {
                throw new CustomizeException(500, "活跃度更新失败");
            }

    }

    /**
     * 签到
     * @param reservationId
     */
    @Override
    @Transactional
    public void signIn(Long reservationId) {
        String userIdStr = UserContext.getCurrentUser();
        Long userId = Long.parseLong(userIdStr);

        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new CustomizeException(404, "预约记录不存在");
        }

        // 校验是否是本人预约
        if (!reservation.getUserId().equals(userId)) {
            throw new CustomizeException(403, "只能签到自己的预约");
        }

        // 校验预约状态
        if (reservation.getStatus() != 1) {
            throw new CustomizeException(400, "只有已预约状态可签到");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = reservation.getStartTime();

        // 校验是否超时（超过预约开始时间15分钟）
        if (Duration.between(startTime, now).toMinutes() > 15) {

            reservation.setStatus(4);  // 4-已违约
            reservationMapper.updateById(reservation);
            try {
                // 超时未签到，自动释放座位，扣除活跃度积分
                UserActivityDTO userActivityDTO = new UserActivityDTO(userId, "ABSENT", reservation.getClassroomId(), reservation.getSeatId());
                userActivityRankService.updateUserActivity(userActivityDTO);
            }catch (Exception e)
            {

                throw new CustomizeException(500, "活跃度更新失败");
            }
            Seat seat = seatMapper.selectById(reservation.getSeatId());
            if (seat != null) {
                seat.setStatus(0);
                seatMapper.updateById(seat);
            }

            // 扣除活跃度积分
            User user = userMapper.selectById(userId);
            if (user != null) {
                user.setActivityScore(user.getActivityScore() + ActivityTypeEnum.ABSENT.score);
                userMapper.updateById(user);
            }

            throw new CustomizeException(400, "预约超时未签到，座位已自动释放，扣除活跃度积分");
        }

        // 签到成功
        reservation.setStatus(2);  // 2-已签到（使用中）
        reservation.setSignInTime(now);
        reservationMapper.updateById(reservation);

        // 更新座位状态为已占用
        Seat seat = seatMapper.selectById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus(2);
            seatMapper.updateById(seat);
        }

        // 增加活跃度积分
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setActivityScore(user.getActivityScore() + ActivityTypeEnum.SIGN_IN.score);
            userMapper.updateById(user);
        }

        try {
            //更新用户活跃度
            UserActivityDTO userActivityDTO = new UserActivityDTO(userId, "SIGN_IN", reservation.getClassroomId(), reservation.getSeatId());
            userActivityRankService.updateUserActivity(userActivityDTO);
        } catch (Exception e) {
            throw new CustomizeException(500, "活跃度更新失败");
        }
    }
     /**
     * 签退
     * @param reservationId
     */
    @Override
    @Transactional
    public void signOut(Long reservationId) {
        String userIdStr = UserContext.getCurrentUser();
        Long userId = Long.parseLong(userIdStr);

        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new CustomizeException(404, "预约记录不存在");
        }

        // 校验是否是本人预约
        if (!reservation.getUserId().equals(userId)) {
            throw new CustomizeException(403, "只能签退自己的预约");
        }

        // 校验预约状态（必须是已签到状态）
        if (reservation.getStatus() != 2) {
            throw new CustomizeException(400, "只有已签到状态可签退");
        }

        LocalDateTime now = LocalDateTime.now();

        // 签退成功
        reservation.setStatus(5);  // 5-已签退
        reservation.setSignOutTime(now);
        reservationMapper.updateById(reservation);

        // 恢复座位状态为空闲
        Seat seat = seatMapper.selectById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus(0);
            seatMapper.updateById(seat);
        }

        // 增加活跃度积分
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setActivityScore(user.getActivityScore() + ActivityTypeEnum.SIGN_OUT.score);
            userMapper.updateById(user);
        }
        try {
            //更新用户活跃度
            UserActivityDTO userActivityDTO = new UserActivityDTO(userId, "SIGN_OUT", reservation.getClassroomId(), reservation.getSeatId());
            userActivityRankService.updateUserActivity(userActivityDTO);
        } catch (Exception e) {
            throw new CustomizeException(500, "活跃度更新失败");
        }
    }
}