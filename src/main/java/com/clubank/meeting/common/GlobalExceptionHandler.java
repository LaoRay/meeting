package com.clubank.meeting.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> handleGlobalException(Exception ex) {
        log.error("服务器异常", ex);
        Map<String, Object> result = new HashMap<>();
        result.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        String msg = "";
        if (ex instanceof MeetingException) {
            msg = ex.getMessage();
        } else {
            msg = "INTERNAL SERVER ERROR";
        }
        result.put("msg", msg);
        return result;
    }
}
