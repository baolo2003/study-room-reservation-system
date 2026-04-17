package com.baolo.study_room_rservation_system.Mapper;

import com.baolo.study_room_rservation_system.Entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取活动积分最高的前N个用户
     * @param limit 前N个
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user WHERE role = 'user' AND status = 1 ORDER BY activity_score DESC LIMIT #{limit}")
    List<User> getTopUsersByActivity(@Param("limit") Integer limit);

    /**
     * 统计活跃用户数（状态为正常的用户）
     * @return 活跃用户数
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE status = 1")
    Integer countActiveUsers();
}
