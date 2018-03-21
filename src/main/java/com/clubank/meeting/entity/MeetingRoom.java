package com.clubank.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeetingRoom {
    private Long roomId;

    private String roomName;

    private Integer roomStatus;

    private String roomExplain;

    private String roomLocation;

    private Integer roomCapacity;
}