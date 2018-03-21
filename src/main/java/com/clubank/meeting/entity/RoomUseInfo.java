package com.clubank.meeting.entity;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RoomUseInfo {
    private Long id;

    private Long roomId;

    private Date startTime;

    private Date endTime;

    private Integer status;

    private Long meetingId;
}