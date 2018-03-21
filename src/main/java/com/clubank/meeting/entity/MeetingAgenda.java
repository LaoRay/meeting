package com.clubank.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeetingAgenda {
    private Long agendaId;

    private Integer agendaSort;

    private String agendaName;

    private String agendaContent;

    private Long meetingId;
}