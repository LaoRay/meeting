package com.clubank.meeting.service;

import com.clubank.meeting.entity.MeetingAgenda;

import java.util.List;

public interface MeetingAgendaService {
    int insertBatch(List<MeetingAgenda> meetingAgendaList);

    int deleteByMeetingId(Long meetingId);
}
