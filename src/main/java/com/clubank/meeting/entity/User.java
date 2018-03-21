package com.clubank.meeting.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User {
    private Long userId;

    private String userAccount;

    private String userName;

    private String password;

    private Integer userStatus;

    private String headIcon;

    private Long departmentId;

    private String departmentLevel;

    private Integer userRole;

    private String userPosition;
}