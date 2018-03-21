package com.clubank.meeting.controller;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.service.MeetingTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/task")
public class MeetingTaskController {

    @Autowired
    private MeetingTaskService meetingTaskService;

    /**
     * 查询个人待办任务(领导待审核，员工待编辑)
     *
     * @param request
     * @return
     */
    @GetMapping("/list/todo")
    public ApiResult findTodoTaskListByUserId(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        return new ApiResult(meetingTaskService.findTodoTaskListByUserId(userId));
    }

    /**
     * 查询个人已办任务
     *
     * @param request
     * @return
     */
    @GetMapping("/list/done")
    public ApiResult findDoneTaskListByUserId(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        return new ApiResult(meetingTaskService.findDoneTaskListByUserId(userId));
    }

    /**
     * 查询个人历史任务
     *
     * @param request
     * @return
     */
    @GetMapping("/list/history")
    public ApiResult findHistoryTaskListByUserId(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        return new ApiResult(meetingTaskService.findHistoryTaskListByUserId(userId));
    }
}
