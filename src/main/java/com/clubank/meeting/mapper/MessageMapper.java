package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper {
    int deleteByPrimaryKey(Long messageId);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(Long messageId);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);

    List<Message> selectMessageListByUserId(@Param("readStatus") Integer readStatus, @Param("userId") Long userId);

    Message selectByMeetingIdAndUserId(@Param("meetingId") Long meetingId, @Param("userId") Long userId);

    List<Long> selectUnreadByMeetingIdAndUserId(@Param("meetingId") Long meetingId, @Param("userId") Long userId);
}