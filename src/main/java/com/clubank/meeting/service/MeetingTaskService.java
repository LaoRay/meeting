package com.clubank.meeting.service;

import com.clubank.meeting.entity.MeetingTask;

import java.util.List;

public interface MeetingTaskService {
    List<MeetingTask> findTodoTaskListByUserId(String userId);

    List<MeetingTask> findDoneTaskListByUserId(String userId);

    List<MeetingTask> findHistoryTaskListByUserId(String userId);
}
