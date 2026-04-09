package com.baolo.study_room_rservation_system.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")//对应数据库表 sys_user
public class User {
    @TableId(type = IdType.AUTO)//告诉MyBatisPlus这个字段是自增的
    private Long id;
    private String studentId;//学号
    private String name;// 姓名
    private String password;//密码
    private String phone;//手机号
    private String preference;//偏好
    private Integer activityScore;//活动积分
    private String role;//角色，看是否是管理员还是 用户
    private Integer status;//状态，0-禁用，1-正常
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//修改时间
}
