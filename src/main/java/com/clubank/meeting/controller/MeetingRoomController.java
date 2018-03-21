package com.clubank.meeting.controller;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.service.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 会议室管理
 */
@RestController
@RequestMapping("/room")
public class MeetingRoomController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @GetMapping("/list")
    public ApiResult meetingRoomList() {
        return new ApiResult(meetingRoomService.meetingRoomList());
    }

    @GetMapping("/list/{date}")
    public ApiResult findMeetingRoomListWithUseInfo(@PathVariable("date") String date) {
        return new ApiResult(meetingRoomService.findMeetingRoomListWithUseInfo(date));
    }
}
