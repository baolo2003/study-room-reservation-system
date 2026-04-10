package com.baolo.study_room_rservation_system.Tool;

import com.baolo.study_room_rservation_system.Exception.CustomizeException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     *
     * 拦截登录注册以外的请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {



        String path = request.getRequestURI();
        System.out.println("拦截器拦截路径: " + path);

        // 1. 从请求头获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomizeException(401, "未登录，请先登录");
        }
        String token = authHeader.substring(7); // 截取Bearer后的令牌

        // 2. 校验令牌合法性
        if (!jwtUtil.validateToken(token)) {
            throw new CustomizeException(401, "令牌无效或已过期");
        }

        // 3. 解析令牌，获取用户信息并存入ThreadLocal
        Claims claims = jwtUtil.parseToken(token);
        Long userId = jwtUtil.getUserId(claims);
        String studentId = jwtUtil.getStudentId(claims);
        // 4. 放行
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
