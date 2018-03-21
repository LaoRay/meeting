package com.clubank.meeting.service.impl;

import com.clubank.meeting.entity.MeetingRoom;
import com.clubank.meeting.entity.RoomUseInfo;
import com.clubank.meeting.mapper.MeetingRoomMapper;
import com.clubank.meeting.mapper.RoomUseInfoMapper;
import com.clubank.meeting.service.MeetingRoomService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MeetingRoomServiceImpl implements MeetingRoomService {

    @Autowired
    private MeetingRoomMapper meetingRoomMapper;
    @Autowired
    private RoomUseInfoMapper roomUseInfoMapper;

    @Override
    public List<MeetingRoom> meetingRoomList() {
        return meetingRoomMapper.selectAllMeetingRoomList();
    }

    @Override
    public List<Map<String, Object>> findMeetingRoomListWithUseInfo(String date) {
        List<Map<String, Object>> result = Lists.newArrayList();
        List<MeetingRoom> meetingRoomList = meetingRoomMapper.selectAllMeetingRoomList();
        if (CollectionUtils.isEmpty(meetingRoomList)) {
            return Lists.newArrayList();
        }
        if (StringUtils.isBlank(date)) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        for (MeetingRoom meetingRoom : meetingRoomList) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("meetingRoom", meetingRoom);
            List<Map<String, Object>> useInfoList = roomUseInfoMapper.selectByRoomId(date, meetingRoom.getRoomId());
            map.put("useInfoList", useInfoList);
            result.add(map);
        }
        return result;
    }

    @Override
    public int saveMeetingRoomUseInfo(RoomUseInfo roomUseInfo) {
        return roomUseInfoMapper.insertSelective(roomUseInfo);
    }

    @Override
    public int deleteByMeetingId(Long meetingId) {
        return roomUseInfoMapper.deleteByMeetingId(meetingId);
    }
}
