package com.clubank.meeting.common;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MeetingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        User user = (User) request.getSession().getAttribute("user");
//        if (user == null) {
//            response.setContentType("application/json;charset=UTF-8");
//            PrintWriter out = response.getWriter();
//            ApiResult apiResult = ApiResult.builder().code(ApiStatusCode.NO_AUTH.getValue()).msg("您还未登录").build();
//            out.write(JSON.toJSONString(apiResult));
//            out.flush();
//            out.close();
//            return false;
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
