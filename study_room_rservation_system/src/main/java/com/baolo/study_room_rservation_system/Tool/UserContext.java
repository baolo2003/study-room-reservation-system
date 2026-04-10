package com.baolo.study_room_rservation_system.Tool;

import org.springframework.stereotype.Component;

@Component
public class UserContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    //设置当前用户ID
    public static void setCurrentUser(String userId) {
        currentUser.set(userId);
    }
   //获取当前用户ID
    public static String getCurrentUser() {
        return currentUser.get();
    }
    //清除当前用户ID,防止内存泄露
    public static void clear() {
        currentUser.remove();
    }
}
