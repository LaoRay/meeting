package com.clubank.meeting.service.impl;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.common.ApiStatusCode;
import com.clubank.meeting.common.MeetingException;
import com.clubank.meeting.entity.Message;
import com.clubank.meeting.entity.UserMessage;
import com.clubank.meeting.mapper.MessageMapper;
import com.clubank.meeting.mapper.UserMessageMapper;
import com.clubank.meeting.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMessageMapper userMessageMapper;

    @Override
    public List<Message> findMessageList(Integer readStatus, Long userId) {
        return messageMapper.selectMessageListByUserId(readStatus, userId);
    }

    @Override
    public Integer findUnreadMessage(Long userId) {
        return userMessageMapper.selectUnreadMessageCount(userId);
    }

    @Override
    public ApiResult updateMessage(UserMessage userMessage) {
        if (userMessage == null || userMessage.getMessageId() == null || userMessage.getUserId() == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "参数错误");
        }
        int result = userMessageMapper.updateByMessageIdAndUserId(userMessage);
        if (result <= 0) {
            throw new MeetingException("消息状态修改异常");
        }
        return new ApiResult();
    }

    @Override
    @Transactional
    public ApiResult updateMessageByMeetingId(Long meetingId, Long userId) {
        List<Long> idList = messageMapper.selectUnreadByMeetingIdAndUserId(meetingId, userId);
        if (!CollectionUtils.isEmpty(idList)) {
            List<UserMessage> userMessageList = idList.stream().distinct().map(id -> userMessageMapper.selectByPrimaryKey(id)).collect(Collectors.toList());
            userMessageList.forEach(userMessage -> userMessage.setIsRead(true));
            userMessageList.forEach(userMessage -> userMessageMapper.updateByPrimaryKeySelective(userMessage));
        }
        return new ApiResult();
    }
}
