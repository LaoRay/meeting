package com.clubank.meeting.service;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.Message;
import com.clubank.meeting.entity.UserMessage;

import java.util.List;

public interface MessageService {
    List<Message> findMessageList(Integer readStatus, Long userId);

    ApiResult updateMessage(UserMessage userMessage);

    Integer findUnreadMessage(Long userId);

    ApiResult updateMessageByMeetingId(Long meetingId, Long userId);
}
