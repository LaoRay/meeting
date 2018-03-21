package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.MeetingAttachment;

public interface MeetingAttachmentMapper {
    int deleteByPrimaryKey(Long attachmentId);

    int insert(MeetingAttachment record);

    int insertSelective(MeetingAttachment record);

    MeetingAttachment selectByPrimaryKey(Long attachmentId);

    int updateByPrimaryKeySelective(MeetingAttachment record);

    int updateByPrimaryKey(MeetingAttachment record);

    int deleteByMeetingId(Long meetingId);
}