package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.User;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectByDepartmentId(Long departmentId);

    List<User> selectUserListLikeDepartmentLevel(String departmentLevel);

    User selectByUserAccount(String userAccount);

    List<User> selectUserLikeName(String name);
}