package com.baolo.study_room_rservation_system.Mapper;

import com.baolo.study_room_rservation_system.Entity.Reservation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 预约Mapper接口
 * 继承BaseMapper，提供基本的CRUD操作
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 统计指定时间之后的预约数量
     * @param startTime 开始时间
     * @return 预约数量
     */
    @Select("SELECT COUNT(*) FROM sys_reservation WHERE create_time >= #{startTime}")
    Integer countReservationsSince(@Param("startTime") LocalDateTime startTime);
}
