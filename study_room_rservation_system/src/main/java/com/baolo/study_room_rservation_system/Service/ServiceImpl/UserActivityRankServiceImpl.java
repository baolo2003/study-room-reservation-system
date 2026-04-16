package com.baolo.study_room_rservation_system.Service.ServiceImpl;

import cn.hutool.core.date.DateUtil;
import com.baolo.study_room_rservation_system.Entity.User;
import com.baolo.study_room_rservation_system.Enum.ActivityRankTimeEnum;
import com.baolo.study_room_rservation_system.Enum.ActivityTypeEnum;
import com.baolo.study_room_rservation_system.Mapper.UserMapper;
import com.baolo.study_room_rservation_system.Service.UserActivityRankService;
import com.baolo.study_room_rservation_system.dto.UserActivityDTO;
import com.baolo.study_room_rservation_system.vo.RankItemVo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserActivityRankServiceImpl implements UserActivityRankService {

    private static final String ACTIVITY_SCORE_KEY = "activity_rank_";//用户活跃度key

    @Autowired
    private UserMapper userMapper;

    @Autowired
   private StringRedisTemplate stringRedisTemplate;
    /**
     * 当天活跃度排行榜
     *
     * @return 当天排行榜key
     */
    private String todayRankKey() {
        return ACTIVITY_SCORE_KEY + DateUtil.format(new Date(), "yyyyMMdd");
    }

    /**
     * 本月排行榜
     *
     * @return 月度排行榜key
     */
    private String monthRankKey() {
        return ACTIVITY_SCORE_KEY + DateUtil.format(new Date(), "yyyyMM");
    }


    /**
     * 更新用户活跃度
     */
    public void updateUserActivity(UserActivityDTO userActivityDTO) {
        //获得用户id
        Long userId = userActivityDTO.getUserId();
        //获得活动类型
        String activityType = userActivityDTO.getActivityType();
        //根据活动类型获得分数
        int score = ActivityTypeEnum.valueOf(activityType).score;
        //获得教室 id
        Long classroomId = userActivityDTO.getClassroomId();
        //获得座位 id
        Long seatId = userActivityDTO.getSeatId();
        //生成filed
        String filed =  classroomId + ":" + seatId;

        final String todayRankKey = todayRankKey();
        final String monthRankKey = monthRankKey();
        //幂等：判断之前是否有更新过相关的活跃度信息
        String useractivitykey=ACTIVITY_SCORE_KEY+userId+DateUtil.format(new Date(), "yyyyMMdd");;
        //String不能强转为integer
      //  Integer ans  = (Integer)stringRedisTemplate.opsForHash().get(useractivitykey, filed);
        Object obj = stringRedisTemplate.opsForHash().get(useractivitykey, filed);
        Integer ans = null;
        if (obj != null) {
            ans = Integer.valueOf(obj.toString());
        }
        if (ans == null)
        {
            //意味着是第一次操作
             if(score>0)
             {
                 //记录加分记录
                 stringRedisTemplate.opsForHash().put(useractivitykey, filed, String.valueOf( score));
                 //设置过期时间为一个月，方便用户查询一个月的记录
                 stringRedisTemplate.expire(useractivitykey, 31, TimeUnit.DAYS);
                 //更新当天/当月排行榜
                 int newans = stringRedisTemplate.opsForZSet().incrementScore(todayRankKey, userId.toString(),score).intValue();
                 stringRedisTemplate.opsForZSet().incrementScore(monthRankKey, userId.toString(), score);

                 /*if(newans<=ans)//这里由于第一次ans为null,所以采用运算会报错
                 {
                     //if判断里的逻辑是为了给日月榜的key设置有效期，日排行榜有效期是31天，而月排行榜有效期是1年
                     // 如果当前用户在日/月排行榜的分数小于等于之前记录的分数，意味着今天第一次添加，则设置有效期
                     stringRedisTemplate.expire(todayRankKey, 31, TimeUnit.DAYS);
                     stringRedisTemplate.expire(monthRankKey, 365, TimeUnit.DAYS);
                 }*/

                 if (stringRedisTemplate.getExpire(todayRankKey) < 0) {
                     stringRedisTemplate.expire(todayRankKey, 31, TimeUnit.DAYS);
                 }
                 if (stringRedisTemplate.getExpire(monthRankKey) < 0) {
                     stringRedisTemplate.expire(monthRankKey, 365, TimeUnit.DAYS);
                 }
             }
        }
       else if (ans>0)//这里只考虑ans大于零的情况，因为积分不会出现负数
        {
            if(score<0)
            {

                //移除活跃榜中的key
                stringRedisTemplate.opsForHash().delete(useractivitykey, filed);
                //更新日月排行榜
                  stringRedisTemplate.opsForZSet().incrementScore(todayRankKey, userId.toString(), score);
                  stringRedisTemplate.opsForZSet().incrementScore(monthRankKey, userId.toString(), score);
            }
        }
    }
   /**
     * 获取用户活跃度
     */
    @Override
    public RankItemVo getUserActivity(Long userId, String type) {
        RankItemVo rankItemVo = new RankItemVo();
        User user = userMapper.selectById(userId);
        BeanUtils.copyProperties(user, rankItemVo);
        //获取日期类型
        ActivityRankTimeEnum activityRankTimeEnum = ActivityRankTimeEnum.valueOf(type);
        switch (activityRankTimeEnum) {
            case DAY:
                // 查询 排名
                Long rank1 = stringRedisTemplate.opsForZSet().reverseRank(todayRankKey(), userId.toString());
                // 查询 分数
                Double score1 = stringRedisTemplate.opsForZSet().score(todayRankKey(), userId.toString());
                rankItemVo.setRank(rank1 == null ? -1 : rank1.intValue() + 1); // 排名从1开始！
                rankItemVo.setScore(score1== null ? 0 : score1.intValue());
                break;
            case MONTH:
                //获取月排行榜
                Long rank2 = stringRedisTemplate.opsForZSet().reverseRank(monthRankKey(), userId.toString());
                // 查询 分数
                Double score2 = stringRedisTemplate.opsForZSet().score(monthRankKey(), userId.toString());
                rankItemVo.setRank(rank2== null ? -1 : rank2.intValue() + 1); // 排名从1开始！
                rankItemVo.setScore(score2 == null ? 0 : score2.intValue());
                break;
        }
        return rankItemVo;
    }

    /**
     * 获取排行榜
     * @param type
     * @param topN
     * @return
     */
    @Override
    public List<RankItemVo> getRankList(String type, int topN) {
        //获取日期类型
        ActivityRankTimeEnum activityRankTimeEnum = ActivityRankTimeEnum.valueOf(type);
        switch (activityRankTimeEnum) {
            case DAY:
                Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(todayRankKey(), 0, topN - 1);// 获取排名

                if (CollectionUtils.isEmpty(typedTuples)) {
                    return new ArrayList<>();
                }
                int rank = 1;
                List<RankItemVo> rankItemVosList = new ArrayList<>();
               //这里因为stream里不允许有变量，有for循环进行遍历
                for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples)
                {
                    String userid = typedTuple.getValue();
                    User user = userMapper.selectById(Long.parseLong(userid));
                    RankItemVo rankItemVo = new RankItemVo();
                    BeanUtils.copyProperties(user, rankItemVo);
                    rankItemVo.setRank(rank++);
                    rankItemVo.setScore(typedTuple.getScore().intValue());
                    rankItemVosList.add(rankItemVo);
                }
                return rankItemVosList;

            case MONTH:
                Set<ZSetOperations.TypedTuple<String>> typedTuples2 = stringRedisTemplate.opsForZSet().reverseRangeWithScores(monthRankKey(), 0, topN - 1);// 获取排名

                if (CollectionUtils.isEmpty(typedTuples2)) {
                    return new ArrayList<>();
                }
                int rank2 = 1;
                List<RankItemVo> rankItemVosList2 = new ArrayList<>();
                for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples2)
                {
                    String userid = typedTuple.getValue();
                    User user = userMapper.selectById(Long.parseLong(userid));
                    RankItemVo rankItemVo = new RankItemVo();
                    BeanUtils.copyProperties(user, rankItemVo);
                    rankItemVo.setRank(rank2++);
                    rankItemVo.setScore(typedTuple.getScore().intValue());
                    rankItemVosList2.add(rankItemVo);
                }
                return rankItemVosList2;
        }
        return null;
    }

}
