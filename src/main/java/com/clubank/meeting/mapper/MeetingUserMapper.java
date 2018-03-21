package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.MeetingUser;

import java.util.List;
import java.util.Map;

public interface MeetingUserMapper {
    int deleteByPrimaryKey(MeetingUser key);

    int insert(MeetingUser record);

    int insertSelective(MeetingUser record);

    MeetingUser selectByPrimaryKey(MeetingUser key);

    int updateByPrimaryKeySelective(MeetingUser record);

    int updateByPrimaryKey(MeetingUser record);

    int deleteByMeetingId(Long meetingId);

    // 查询所有参会人
    List<Map<String, Object>> selectUserListByMeetingId(Long meetingId);

    // 查询除放弃参会外的参会人
    List<Map<String, Object>> selectAttendeeListByMeetingId(Long meetingId);
}