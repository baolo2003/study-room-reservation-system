package com.baolo.study_room_rservation_system.Tool;

import com.baolo.study_room_rservation_system.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<User> currentUserObj = new ThreadLocal<>();

    //设置当前用户ID
    public static void setCurrentUser(String userId) {
        currentUser.set(userId);
    }

    //设置当前用户对象
    public static void setCurrentUser(User user) {
        currentUserObj.set(user);
        if (user != null && user.getId() != null) {
            currentUser.set(String.valueOf(user.getId()));
        }
    }

    //获取当前用户ID (String)
    public static String getCurrentUser() {
        return currentUser.get();
    }

    //获取当前用户ID (Long)
    public static Long getCurrentUserId() {
        String userId = currentUser.get();
        return userId != null ? Long.parseLong(userId) : null;
    }

    //获取当前用户对象
    public static User getCurrentUserObj() {
        return currentUserObj.get();
    }

    //清除当前用户ID,防止内存泄露
    public static void clear() {
        currentUser.remove();
        currentUserObj.remove();
    }
}
