package com.baolo.study_room_rservation_system.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice//spring 通过这个注解去监听项目所有 Controller 抛出的异常
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 业务异常处理
     */

    @ExceptionHandler(CustomizeException.class)
    private Map<String,Object>handleExpection(CustomizeException e)
    {
        Map<String,Object>resutl=new HashMap<>();
        resutl.put("code",e.getCode());
        resutl.put("msg",e.getMessage());
        resutl.put("data",null);
        log.error("业务异常: {}", e.getMessage());
        return resutl;
    }


    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", "服务器内部异常");
        result.put("data", null);
        log.error("系统异常: {}", e.getMessage());
        return result;
    }
}
