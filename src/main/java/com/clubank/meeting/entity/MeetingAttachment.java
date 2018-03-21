package com.clubank.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeetingAttachment {
    private Long attachmentId;

    private String attachmentUrl;

    private String attachmentName;

    private Integer attachmentStatus;

    private Long meetingId;
}