package com.baolo.study_room_rservation_system.Service;

import com.baolo.study_room_rservation_system.dto.ReservationDTO;
import com.baolo.study_room_rservation_system.dto.SeatQueryDTO;
import com.baolo.study_room_rservation_system.vo.SeatDetailVO;
import com.baolo.study_room_rservation_system.vo.SeatVO;

import java.util.List;

public interface SeatService {

    /**
     * 查询座位列表（支持多条件筛选）
     */
    List<SeatVO> querySeats(SeatQueryDTO queryDTO);

    /**
     * 获取座位详情
     */
    SeatDetailVO getSeatDetail(Long seatId);

    /**
     * 获取座位分布图（按教室分组）
     */
    List<SeatVO> getSeatMap(Long classroomId);

    /**
     * 预约座位
     */
    void reserveSeat(ReservationDTO reservationDTO);

    /**
     * 取消预约
     */
    void cancelReservation(Long reservationId);

    /**
     * 签到
     */
    void signIn(Long reservationId);

    /**
     * 签退
     */
    void signOut(Long reservationId);
}