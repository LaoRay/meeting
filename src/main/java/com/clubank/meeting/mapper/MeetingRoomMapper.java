package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.MeetingRoom;

import java.util.List;

public interface MeetingRoomMapper {
    int deleteByPrimaryKey(Long roomId);

    int insert(MeetingRoom record);

    int insertSelective(MeetingRoom record);

    MeetingRoom selectByPrimaryKey(Long roomId);

    int updateByPrimaryKeySelective(MeetingRoom record);

    int updateByPrimaryKey(MeetingRoom record);

    List<MeetingRoom> selectAllMeetingRoomList();
}