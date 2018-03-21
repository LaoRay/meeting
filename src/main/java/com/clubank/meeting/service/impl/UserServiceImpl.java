package com.clubank.meeting.service.impl;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.common.ApiStatusCode;
import com.clubank.meeting.common.MeetingException;
import com.clubank.meeting.entity.Department;
import com.clubank.meeting.entity.User;
import com.clubank.meeting.mapper.DepartmentMapper;
import com.clubank.meeting.mapper.UserMapper;
import com.clubank.meeting.service.UserService;
import com.clubank.meeting.utils.DepartmentLevelUtil;
import com.clubank.meeting.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public ApiResult saveUser(User user) {
        if (user == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "user is null");
        }
        int result = 0;
        if (user.getUserId() == null) {
            // 新增用户密码加密
            user.setPassword(MD5Util.encrypt(user.getPassword()));
            result = userMapper.insertSelective(user);
        } else {
            userMapper.updateByPrimaryKeySelective(user);
        }
        return result > 0 ? new ApiResult() : new ApiResult("failed save user");
    }

    @Override
    public User findUserById(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public List<User> findUserListByDepartmentId(Long departmentId) {
        Department department = departmentMapper.selectByPrimaryKey(departmentId);
        if (department == null) {
            throw new MeetingException("ID为" + departmentId + "的部门不存在");
        }
        List<User> userList = userMapper.selectByDepartmentId(departmentId);
        List<User> subDeptUserList = userMapper.selectUserListLikeDepartmentLevel(DepartmentLevelUtil.calculateLevel(department.getDepartmentLevel(), departmentId));
        userList.addAll(subDeptUserList);
        return userList;
    }

    @Override
    public ApiResult login(User user, HttpSession session) {
        if (user == null || StringUtils.isBlank(user.getUserAccount()) || StringUtils.isBlank(user.getPassword())) {
            return ApiResult.builder().code(ApiStatusCode.PARRAM_ERROR.getValue()).msg("账号或密码不能为空").build();
        }
        User dbUser = userMapper.selectByUserAccount(user.getUserAccount());
        if (dbUser == null) {
            return new ApiResult(ApiStatusCode.NOT_FOUND.getValue(), "账号或密码错误");
        }
        if (dbUser.getUserStatus() != 1) {
            return new ApiResult(ApiStatusCode.DATA_INVALID.getValue(), "账号已无效，请联系管理员");
        }
        if (!MD5Util.encrypt(user.getPassword()).equals(dbUser.getPassword())) {
            return new ApiResult(ApiStatusCode.NOT_FOUND.getValue(), "账号或密码错误");
        }
        session.setAttribute("user", dbUser);
        return new ApiResult(dbUser);
    }

    @Override
    public List<User> findUserListByName(String name) {
        return userMapper.selectUserLikeName(name);
    }
}
