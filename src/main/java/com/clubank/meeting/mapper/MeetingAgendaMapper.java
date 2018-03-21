package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.MeetingAgenda;

public interface MeetingAgendaMapper {
    int deleteByPrimaryKey(Long agendaId);

    int insert(MeetingAgenda record);

    int insertSelective(MeetingAgenda record);

    MeetingAgenda selectByPrimaryKey(Long agendaId);

    int updateByPrimaryKeySelective(MeetingAgenda record);

    int updateByPrimaryKey(MeetingAgenda record);

    int deleteByMeetingId(Long meetingId);
}