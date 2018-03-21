package com.clubank.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Department {
    private Long departmentId;

    private String departmentLevel;

    private int departmentSort;

    private Long parentId;

    private String departmentName;

    private Integer departmentStatus;

    private String departmentExplain;

    private Long departmentLeader;
}