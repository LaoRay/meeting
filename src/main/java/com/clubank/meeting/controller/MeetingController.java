package com.clubank.meeting.controller;

import com.alibaba.fastjson.JSONObject;
import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.MeetingUser;
import com.clubank.meeting.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 会议管理
 */
@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    /**
     * 领导发起会议
     *
     * @param meetingObject
     * @return
     */
    @PostMapping("/leader/startApply")
    public ApiResult leaderStartMeetingApply(@RequestBody JSONObject meetingObject, HttpServletRequest request) {
        // 当前用户id
        Long userId = Long.valueOf(request.getHeader("userId"));
        return meetingService.leaderStartMeetingApply(meetingObject, userId);
    }

    /**
     * 员工发起会议申请
     *
     * @param meetingObject
     * @param request
     * @return
     */
    @PostMapping("/staff/startApply")
    public ApiResult staffStartMeetingApply(@RequestBody JSONObject meetingObject, HttpServletRequest request) {
        // 当前用户id
        Long userId = Long.valueOf(request.getHeader("userId"));
        return meetingService.staffStartMeetingApply(meetingObject, userId);
    }

    /**
     * 组织人编辑并提交审批
     *
     * @param meetingObject
     * @return
     */
    @PostMapping("/organizer/updateAndCommit")
    public ApiResult organizerUpdateMeetingAndCommit(@RequestBody JSONObject meetingObject, HttpServletRequest request) {
        // 当前用户id
        Long userId = Long.valueOf(request.getHeader("userId"));
        return meetingService.organizerUpdateMeetingAndCommit(meetingObject, userId);
    }

    /**
     * 领导审批
     *
     * @param param
     * @return
     */
    @PostMapping("/leader/approval")
    public ApiResult leaderApproval(@RequestBody JSONObject param, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return meetingService.leaderApproval(param, userId);
    }

    /**
     * 会议详情
     *
     * @param meetingId
     * @return
     */
    @GetMapping("/details/{meetingId}")
    public ApiResult getMeetingDetailsByMeetingId(@PathVariable("meetingId") Long meetingId) {
        return new ApiResult(meetingService.selectWithDetailsByMeetingId(meetingId));
    }

    /**
     * 根据会议ID查询参会人列表
     *
     * @param meetingId
     * @return
     */
    @GetMapping("/userList/{meetingId}")
    public ApiResult findUserListByMeetingId(@PathVariable("meetingId") Long meetingId) {
        return new ApiResult(meetingService.findUserListByMeetingId(meetingId));
    }

    /**
     * 我创建的会议
     *
     * @param request
     * @return
     */
    @GetMapping("/list/create")
    public ApiResult findMeetingListByCreateUserId(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(meetingService.findMeetingListByCreateUserId(userId));
    }

    /**
     * 指派给我的会议
     *
     * @param request
     * @return
     */
    @GetMapping("/list/organizer")
    public ApiResult findMeetingListByOrganizerId(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(meetingService.findMeetingListByOrganizerId(userId));
    }

    /**
     * 查询已处理的会议
     *
     * @param request
     * @return
     */
    @GetMapping("/list/handled")
    public ApiResult findHandledMeetingList(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(meetingService.findHandledMeetingList(userId));
    }

    /**
     * 取消会议申请
     *
     * @param meetingId
     * @param request
     * @return
     */
    @PostMapping("/cancelApply")
    public ApiResult cancelMeetingApply(@RequestBody JSONObject meetingId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return meetingService.cancelMeetingApply(meetingId.getLongValue("meetingId"), userId);
    }

    /**
     * 取消已发布会议
     *
     * @param meetingId
     * @param request
     * @return
     */
    @PostMapping("/cancelPublished")
    public ApiResult cancelPublishedMeeting(@RequestBody JSONObject meetingId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return meetingService.cancelPublishedMeeting(meetingId.getLongValue("meetingId"), userId);
    }

    /**
     * 领导查询已审批会议(1 已批准 2 未通过)
     *
     * @param request
     * @return
     */
    @GetMapping("/list/approved/{type}")
    public ApiResult findApprovedMeetingByReviewId(@PathVariable("type") Integer type, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(meetingService.findApprovedMeetingByReviewId(userId, type));
    }

    /**
     * 参会人确认是否参会
     *
     * @param meetingUser
     * @param request
     * @return
     */
    @PostMapping("/confirm")
    public ApiResult confirmWhetherAttend(@RequestBody MeetingUser meetingUser, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return meetingService.confirmWhetherAttend(meetingUser, userId);
    }

    /**
     * 查询已参加会议
     *
     * @param request
     * @return
     */
    @GetMapping("/list/attended")
    public ApiResult findAttendedMeetingList(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(meetingService.findAttendedMeetingList(userId));
    }

    /**
     * 查询已放弃参加的会议
     *
     * @param request
     * @return
     */
    @GetMapping("/list/giveup")
    public ApiResult findGiveUpMeetingList(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(meetingService.findGiveUpMeetingList(userId));
    }

    /**
     * 查询待参加的会议
     *
     * @param request
     * @return
     */
    @GetMapping("/list/toAttend")
    public ApiResult findToAttendMeetingList(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(meetingService.findToAttendMeetingList(userId));
    }

    /**
     * 一键催审
     *
     * @param param
     * @param request
     * @return
     */
    @PostMapping("/remind")
    public ApiResult remindLeaderReview(@RequestBody JSONObject param, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return meetingService.remindLeaderReview(param, userId);
    }
}
