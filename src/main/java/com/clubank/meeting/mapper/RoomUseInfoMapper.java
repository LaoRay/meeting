package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.RoomUseInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoomUseInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoomUseInfo record);

    int insertSelective(RoomUseInfo record);

    RoomUseInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoomUseInfo record);

    int updateByPrimaryKey(RoomUseInfo record);

    List<Map<String, Object>> selectByRoomId(@Param("date") String date, @Param("roomId") Long roomId);

    int deleteByMeetingId(Long meetingId);

    int updateByMeetingId(@Param("meetingId") Long meetingId, @Param("status") Integer status);

    List<RoomUseInfo> selectConflictList(@Param("roomId") Long roomId, @Param("startTime") String startTime, @Param("endTime") String endTime);
}