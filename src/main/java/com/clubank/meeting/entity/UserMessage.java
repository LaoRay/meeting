package com.clubank.meeting.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserMessage {

    private Long id;

    private Long messageId;

    private Long userId;

    private Boolean isDelete;

    private Boolean isRead;
}