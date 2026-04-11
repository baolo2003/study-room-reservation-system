package com.baolo.study_room_rservation_system.WebMvcConfig;

import com.baolo.study_room_rservation_system.Tool.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 拦截所有接口
                .addPathPatterns("/api/**")
                // 排除无需拦截的接口
                .excludePathPatterns(
                        "/api/user/register",  // 注册
                        "/api/user/login",     // 登录
                        "/api/user/forgetPwd",  // 找回密码
                        "/api/user/refreshtoken"// 刷新令牌
                );
    }
}
