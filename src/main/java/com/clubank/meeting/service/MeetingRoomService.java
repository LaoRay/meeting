package com.clubank.meeting.service;

import com.clubank.meeting.entity.MeetingRoom;
import com.clubank.meeting.entity.RoomUseInfo;

import java.util.List;
import java.util.Map;

public interface MeetingRoomService {
    List<MeetingRoom> meetingRoomList();

    List<Map<String, Object>> findMeetingRoomListWithUseInfo(String date);

    int saveMeetingRoomUseInfo(RoomUseInfo roomUseInfo);

    int deleteByMeetingId(Long meetingId);
}
