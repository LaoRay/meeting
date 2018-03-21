package com.clubank.meeting.service.impl;

import com.clubank.meeting.entity.MeetingUser;
import com.clubank.meeting.mapper.MeetingUserMapper;
import com.clubank.meeting.service.MeetingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeetingUserServiceImpl implements MeetingUserService {

    @Autowired
    private MeetingUserMapper meetingUserMapper;

    @Override
    @Transactional
    public int insertBatch(List<MeetingUser> meetingUserList) {
        int result = 0;
        for (MeetingUser meetingUser : meetingUserList) {
            int insert = meetingUserMapper.insertSelective(meetingUser);
            result += insert;
        }
        return result;
    }

    @Override
    public int deleteByMeetingId(Long meetingId) {
        return meetingUserMapper.deleteByMeetingId(meetingId);
    }
}
