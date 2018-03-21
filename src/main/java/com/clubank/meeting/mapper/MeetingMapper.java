package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.Meeting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MeetingMapper {
    int deleteByPrimaryKey(Long meetingId);

    int insert(Meeting record);

    int insertSelective(Meeting record);

    Meeting selectByPrimaryKey(Long meetingId);

    int updateByPrimaryKeySelective(Meeting record);

    int updateByPrimaryKey(Meeting record);

    Meeting selectWithDetailsByPrimaryKey(Long meetingId);

    List<Meeting> selectMeetingListByCreateUserId(Long userId);

    List<Meeting> selectMeetingListByOrganizerId(Long userId);

    List<Meeting> selectHandledMeetingListByUserId(@Param("userId") Long userId, @Param("userRole") Integer userRole);

    List<Meeting> selectApprovedMeetingByReviewId(@Param("userId") Long userId, @Param("status") Integer status);

    List<Meeting> selectToAttendMeetingList(Long userId);

    List<Meeting> selectAttendedMeetingList(Long userId);

    List<Meeting> selectGiveUpMeetingList(Long userId);

    //会议结束后将状态置为已结束
    int updateFinishMeetingStatus();

    //查询即将开始的会议
    List<Meeting> selectAboutToStartMeeting();
}