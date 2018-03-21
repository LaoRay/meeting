package com.clubank.meeting.service;

import com.clubank.meeting.entity.MeetingAttachment;

import java.util.List;

public interface MeetingAttachmentService {
    int insertBatch(List<MeetingAttachment> attachmentList);

    int deleteByMeetingId(Long meetingId);
}
