package com.baolo.study_room_rservation_system.controller;

import com.baolo.study_room_rservation_system.Service.SeatService;
import com.baolo.study_room_rservation_system.Tool.Result;
import com.baolo.study_room_rservation_system.dto.ReservationDTO;
import com.baolo.study_room_rservation_system.dto.SeatQueryDTO;
import com.baolo.study_room_rservation_system.vo.SeatDetailVO;
import com.baolo.study_room_rservation_system.vo.SeatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 座位管理 Controller
 * 提供座位查询、预约、取消、签到、签退功能
 */
@RestController
@RequestMapping("/api/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    /**
     * 2.3.1 座位查询与展示
     * 支持按自习室区域、座位状态、座位属性筛选
     */
    @PostMapping("/list")
    public Result<List<SeatVO>> querySeats(@RequestBody SeatQueryDTO queryDTO) {
        List<SeatVO> seats = seatService.querySeats(queryDTO);
        return Result.success(seats);
    }

    /**
     * 获取座位详情
     */
    @GetMapping("/detail/{seatId}")//@PathVariable的作用是将路径中的参数seatId绑定到queryDTO的seatId属性上
    public Result<SeatDetailVO> getSeatDetail(@PathVariable Long seatId) {
        SeatDetailVO detail = seatService.getSeatDetail(seatId);
        return Result.success(detail);
    }

    /**
     * 获取座位分布图
     */
    @GetMapping("/map")//@RequestParam的作用是将Query Params中的参数classroomId绑定到queryDTO的classroomId属性上
    public Result<List<SeatVO>> getSeatMap(@RequestParam(required = false) Long classroomId) {
        List<SeatVO> seatMap = seatService.getSeatMap(classroomId);
        return Result.success(seatMap);
    }

    /**
     * 2.3.2 座位预约
     */
    @PostMapping("/reserve")
    public Result<Void> reserveSeat(@RequestBody ReservationDTO reservationDTO) {
        seatService.reserveSeat(reservationDTO);
        return Result.success(null);
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel/{reservationId}")
    public Result<Void> cancelReservation(@PathVariable Long reservationId) {
        seatService.cancelReservation(reservationId);
        return Result.success(null);
    }

    /**
     * 签到
     */
    @PostMapping("/signIn/{reservationId}")
    public Result<Void> signIn(@PathVariable Long reservationId) {
        seatService.signIn(reservationId);
        return Result.success(null);
    }

    /**
     * 签退
     */
    @PostMapping("/signOut/{reservationId}")
    public Result<Void> signOut(@PathVariable Long reservationId) {
        seatService.signOut(reservationId);
        return Result.success(null);
    }
}