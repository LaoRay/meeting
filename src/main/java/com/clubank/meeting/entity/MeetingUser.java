package com.clubank.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeetingUser {
    private Long meetingId;

    private Long userId;

    private Integer confirmStatus;

    private Integer meetingRole;
}