package com.clubank.meeting.service.impl;

import com.clubank.meeting.entity.MeetingAgenda;
import com.clubank.meeting.mapper.MeetingAgendaMapper;
import com.clubank.meeting.service.MeetingAgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeetingAgendaServiceImpl implements MeetingAgendaService {

    @Autowired
    private MeetingAgendaMapper meetingAgendaMapper;

    @Override
    @Transactional
    public int insertBatch(List<MeetingAgenda> meetingAgendaList) {
        int result = 0;
        for (MeetingAgenda meetingAgenda : meetingAgendaList) {
            int insert = meetingAgendaMapper.insertSelective(meetingAgenda);
            result += insert;
        }
        return result;
    }

    @Override
    public int deleteByMeetingId(Long meetingId) {
        return meetingAgendaMapper.deleteByMeetingId(meetingId);
    }
}
