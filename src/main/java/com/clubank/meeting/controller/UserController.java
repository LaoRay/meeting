package com.clubank.meeting.controller;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.User;
import com.clubank.meeting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @PostMapping("/save")
    public ApiResult saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    /**
     * 根据id查询用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("/info")
    public ApiResult findUserById(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(userService.findUserById(userId));
    }

    /**
     * 根据部门id查询部门所有员工
     *
     * @param departmentId
     * @return
     */
    @GetMapping("/list/{departmentId}")
    public ApiResult findUserListByDepartmentId(@PathVariable("departmentId") Long departmentId) {
        return new ApiResult(userService.findUserListByDepartmentId(departmentId));
    }

    /**
     * 根据用户名模糊查询用户
     *
     * @param name
     * @return
     */
    @GetMapping("/search/{name}")
    public ApiResult findUserListByName(@PathVariable("name") String name) {
        return new ApiResult(userService.findUserListByName(name));
    }
}
