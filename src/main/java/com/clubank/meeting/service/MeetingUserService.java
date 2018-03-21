package com.clubank.meeting.service;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.Meeting;
import com.clubank.meeting.entity.MeetingUser;

import java.util.List;

public interface MeetingUserService {
    int insertBatch(List<MeetingUser> meetingUserList);

    int deleteByMeetingId(Long meetingId);
}
