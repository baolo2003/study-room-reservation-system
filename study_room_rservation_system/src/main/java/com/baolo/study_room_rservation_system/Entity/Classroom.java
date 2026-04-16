package com.baolo.study_room_rservation_system.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_classroom")
public class Classroom {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String openTime;// 开放时间
    private Integer status;
    private LocalDateTime createTime;
    private Integer seatCount;
}
