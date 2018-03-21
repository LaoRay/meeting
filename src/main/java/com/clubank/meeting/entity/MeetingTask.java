package com.clubank.meeting.entity;

import lombok.*;
import org.springframework.beans.BeanUtils;

/**
 * 自定义任务类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MeetingTask extends Meeting {

    private String taskId;

    private String taskName;

    public static MeetingTask adapt(Meeting meeting) {
        MeetingTask meetingTask = new MeetingTask();
        BeanUtils.copyProperties(meeting, meetingTask);
        return meetingTask;
    }
}
