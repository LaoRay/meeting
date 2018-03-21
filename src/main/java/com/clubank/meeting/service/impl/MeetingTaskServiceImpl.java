package com.clubank.meeting.service.impl;

import com.clubank.meeting.entity.MeetingTask;
import com.clubank.meeting.mapper.MeetingMapper;
import com.clubank.meeting.service.MeetingTaskService;
import com.google.common.collect.Lists;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class MeetingTaskServiceImpl implements MeetingTaskService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private MeetingMapper meetingMapper;

    @Override
    public List<MeetingTask> findTodoTaskListByUserId(String userId) {
        // 待办任务
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        if (CollectionUtils.isEmpty(taskList)) {
            return Lists.newArrayList();
        }
        List<MeetingTask> meetingTaskList = Lists.newArrayList();
        for (Task task : taskList) {
            Long meetingId = getTaskVariable(task.getId(), "meetingId");
            MeetingTask meetingTask = MeetingTask.adapt(meetingMapper.selectByPrimaryKey(meetingId));
            meetingTask.setTaskId(task.getId());
            meetingTask.setTaskName(task.getName());
            meetingTaskList.add(meetingTask);
        }
        return meetingTaskList;
    }

    @Override
    public List<MeetingTask> findDoneTaskListByUserId(String userId) {
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId).orderByTaskCreateTime().desc().list();
        if (CollectionUtils.isEmpty(historicTaskInstanceList)) {
            return Lists.newArrayList();
        }
        List<MeetingTask> meetingTaskList = Lists.newArrayList();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            // 已办任务
            // 如果该任务对应的流程实例在运行时任务表里查询到，说明就是这个流程实例未走完 并且用用户id以及任务id在运行时候任务表里查询不到结果 才算是已办任务
            if ((taskService.createTaskQuery().processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult() != null)
                    && (taskService.createTaskQuery().taskCandidateUser(userId).taskId(historicTaskInstance.getId()).list().size() == 0)) {
                meetingTaskList.add(getMeetingTask(historicTaskInstance));
            }
        }
        return meetingTaskList;
    }

    private MeetingTask getMeetingTask(HistoricTaskInstance historicTaskInstance) {
        Long meetingId = getHistoryVariable(historicTaskInstance.getProcessInstanceId(), "meetingId");
        MeetingTask meetingTask = MeetingTask.adapt(meetingMapper.selectByPrimaryKey(meetingId));
        meetingTask.setTaskId(historicTaskInstance.getId());
        meetingTask.setTaskName(historicTaskInstance.getName());
        return meetingTask;
    }

    @Override
    public List<MeetingTask> findHistoryTaskListByUserId(String userId) {
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId).orderByTaskCreateTime().desc().list();
        if (CollectionUtils.isEmpty(historicTaskInstanceList)) {
            return Lists.newArrayList();
        }
        List<MeetingTask> meetingTaskList = Lists.newArrayList();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            // 历史任务
            // 如果该任务对应的流程实例在运行时任务表里查询不到，说明就是这个流程实例已经走完
            if (taskService.createTaskQuery().processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult() == null) {
                meetingTaskList.add(getMeetingTask(historicTaskInstance));
            }
        }
        return meetingTaskList;
    }

    private Long getTaskVariable(String taskId, String varibleName) {
        Object variable = taskService.getVariable(taskId, varibleName);
        if (variable == null) {
            return null;
        }
        return (Long) variable;
    }

    private Long getHistoryVariable(String processInstanceId, String varibleName) {
        HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).variableName(varibleName).singleResult();
        if (historicVariableInstance == null || historicVariableInstance.getValue() == null) {
            return null;
        }
        return (Long) historicVariableInstance.getValue();
    }
}
