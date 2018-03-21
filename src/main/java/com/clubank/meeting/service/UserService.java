package com.clubank.meeting.service;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.User;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface UserService {
    ApiResult saveUser(User user);

    User findUserById(Long userId);

    List<User> findUserListByDepartmentId(Long departmentId);

    ApiResult login(User user, HttpSession session);

    List<User> findUserListByName(String name);
}
