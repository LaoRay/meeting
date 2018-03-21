package com.clubank.meeting.entity;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Message {

    public static final String REVIEW = "1";//您有新的会议审核请求
    public static final String EDIT = "2";//您有新的会议编辑任务
    public static final String INVITE = "3";//您有新的会议邀请
    public static final String PASS = "4";//您的会议申请已通过
    public static final String REJECT = "5";//您的会议申请被否决
    public static final String CANCEL = "6";//您的会议已取消
    public static final String REMIND = "7";//您的会议即将开始

    public static final Integer SYS_NOTIC = 1;//系统通知
    public static final Integer USER_TASK = 2;//流程任务
    public static final Integer REMINDER = 3;//会议催审

    private Long messageId;

    private Long meetingId;

    private String taskId;

    private Integer messageType;

    private String messageTitle;

    private String messageContent;

    private String description;

    private Date createTime;

    private Long createUserId;

    private String createUserName;

    // 消息关联的用户
    private UserMessage userMessage;
}