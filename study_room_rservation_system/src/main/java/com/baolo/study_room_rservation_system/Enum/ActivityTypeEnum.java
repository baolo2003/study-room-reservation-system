package com.baolo.study_room_rservation_system.Enum;

public enum ActivityTypeEnum {
    SIGN_IN(2),//签到
    RESERVE(1),//预约
    SIGN_OUT(3),//签退
    CANCEL(1),//取消预约
    ABSENT(-3);//缺席

    public final int score;

    ActivityTypeEnum(int score) {
        this.score = score;
    }
}
