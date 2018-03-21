package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.UserMessage;

public interface UserMessageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserMessage userMessage);

    int insertSelective(UserMessage userMessage);

    UserMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserMessage userMessage);

    int updateByPrimaryKey(UserMessage record);

    Integer selectUnreadMessageCount(Long userId);

    int updateByMessageIdAndUserId(UserMessage userMessage);
}