package com.baolo.study_room_rservation_system.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
     private Long id;
     private Long userId;
     private Long seatId;
     private Long classroomId;
     private LocalDateTime startTime;
     private LocalDateTime endTime;
     private Integer status;
     private LocalDateTime createTime;
     private LocalDateTime signInTime;
     private LocalDateTime signOutTime;

// // 非数据库字段：关联座位信息
// @TableField(exist = false)
// private Seat seat;
}
