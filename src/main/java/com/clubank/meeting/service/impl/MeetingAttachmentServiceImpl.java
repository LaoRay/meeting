package com.clubank.meeting.service.impl;

import com.clubank.meeting.entity.MeetingAttachment;
import com.clubank.meeting.mapper.MeetingAttachmentMapper;
import com.clubank.meeting.service.MeetingAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeetingAttachmentServiceImpl implements MeetingAttachmentService {

    @Autowired
    private MeetingAttachmentMapper meetingAttachmentMapper;

    @Override
    @Transactional
    public int insertBatch(List<MeetingAttachment> attachmentList) {
        int result = 0;
        for (MeetingAttachment meetingAttachment : attachmentList) {
            int insert = meetingAttachmentMapper.insertSelective(meetingAttachment);
            result += insert;
        }
        return result;
    }

    @Override
    public int deleteByMeetingId(Long meetingId) {
        return meetingAttachmentMapper.deleteByMeetingId(meetingId);
    }
}
