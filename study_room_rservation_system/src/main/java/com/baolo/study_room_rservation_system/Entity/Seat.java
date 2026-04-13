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
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_seat")
public class Seat {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long classroomId;
    private String seatNo;
    private Integer isWindow;
    private Integer hasSocket;
    private Integer isQuiet;
    private Integer status;
    private Integer version;
    private LocalDateTime createTime;

    // 非数据库字段：教室名称（用于详情展示）
    @TableField(exist = false)
    private String classroomName;
}
