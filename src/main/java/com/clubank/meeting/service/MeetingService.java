package com.clubank.meeting.service;

import com.alibaba.fastjson.JSONObject;
import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.Meeting;
import com.clubank.meeting.entity.MeetingUser;

import java.util.List;
import java.util.Map;

public interface MeetingService {
    ApiResult leaderStartMeetingApply(JSONObject meetingObject, Long userId);

    ApiResult organizerUpdateMeetingAndCommit(JSONObject meetingObject, Long userId);

    ApiResult staffStartMeetingApply(JSONObject meetingObject, Long userId);

    ApiResult leaderApproval(JSONObject param, Long userId);

    Meeting selectWithDetailsByMeetingId(Long meetingId);

    List<Meeting> findMeetingListByCreateUserId(Long userId);

    List<Meeting> findMeetingListByOrganizerId(Long userId);

    List<Meeting> findHandledMeetingList(Long userId);

    List<Map<String, Object>> findUserListByMeetingId(Long meetingId);

    ApiResult cancelMeetingApply(Long meetingId, Long userId);

    ApiResult cancelPublishedMeeting(Long meetingId, Long userId);

    List<Meeting> findApprovedMeetingByReviewId(Long userId, Integer type);

    ApiResult confirmWhetherAttend(MeetingUser meetingUser, Long userId);

    List<Meeting> findAttendedMeetingList(Long userId);

    List<Meeting> findGiveUpMeetingList(Long userId);

    List<Meeting> findToAttendMeetingList(Long userId);

    ApiResult remindLeaderReview(JSONObject param, Long userId);

    void remindUserToAttendMeeting();
}
