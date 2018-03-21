package com.clubank.meeting.controller;

import com.alibaba.fastjson.JSONObject;
import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.UserMessage;
import com.clubank.meeting.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/message")
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 查询消息列表
     *
     * @param readStatus 0未读 1已读 -1全部
     * @param request
     * @return
     */
    @GetMapping("/list/{readStatus}")
    public ApiResult findMessageList(@PathVariable("readStatus") Integer readStatus, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(messageService.findMessageList(readStatus, userId));
    }

    /**
     * 查询未读消息个数
     *
     * @param request
     * @return
     */
    @GetMapping("/unread")
    public ApiResult findUnreadMessage(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(messageService.findUnreadMessage(userId));
    }

    /**
     * 消息状态修改(已读，删除)
     *
     * @param userMessage
     * @return
     */
    @PostMapping("/update")
    public ApiResult updateMessage(@RequestBody UserMessage userMessage) {
        return messageService.updateMessage(userMessage);
    }

    /**
     * 根据会议主键更新对应消息状态为已读
     *
     * @param param
     * @param request
     * @return
     */
    @PostMapping("/update/read")
    public ApiResult updateMessageByMeetingId(@RequestBody JSONObject param, HttpServletRequest request) {
        Long meetingId = param.getLongValue("meetingId");
        Long userId = Long.valueOf(request.getHeader("userId"));
        return new ApiResult(messageService.updateMessageByMeetingId(meetingId, userId));
    }
}
