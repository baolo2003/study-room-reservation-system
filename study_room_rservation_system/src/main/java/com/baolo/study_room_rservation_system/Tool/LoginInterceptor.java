package com.baolo.study_room_rservation_system.Tool;

import com.baolo.study_room_rservation_system.Exception.CustomizeException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     *
     * 拦截登录注册以外的请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {


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

        Long userId = jwtUtil.getUserId(jwtUtil.parseToken(token));
        //查看redis中是否存在该token
        String accessKeyToken = stringRedisTemplate.opsForValue().get("ACCESS_TOKEN_KEY"+userId);

       if (accessKeyToken == null || !accessKeyToken.equals(token))
       {
           throw new CustomizeException(401, "您已登出或token失效");
       }
        // 3. 解析令牌，获取用户信息并存入ThreadLocal
        UserContext.setCurrentUser(String.valueOf(userId));
        // 4. 放行
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
