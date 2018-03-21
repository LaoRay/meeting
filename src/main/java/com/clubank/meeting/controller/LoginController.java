package com.clubank.meeting.controller;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.User;
import com.clubank.meeting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResult login(@RequestBody User user, HttpServletRequest request) {
        HttpSession session = request.getSession();
        return userService.login(user, session);
    }

    @PostMapping("/logout")
    private ApiResult logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return new ApiResult();
    }
}
