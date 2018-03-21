package com.clubank.meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Meeting {

    /**
     * 待审核
     */
    public static final Integer TO_REVIEW = 1;
    /**
     * 待编辑
     */
    public static final Integer TO_EDIT = 2;
    /**
     * 已发布
     */
    public static final Integer PUBLISHED = 3;
    /**
     * 已结束
     */
    public static final Integer FINISHED = 4;
    /**
     * 已取消
     */
    public static final Integer CANCELED = 5;
    /**
     * 审核不通过
     */
    public static final Integer REJECT = 6;

    private Long meetingId;

    private Long roomId;

    private String meetingRoom;

    private Long organizerId;

    private String organizerName;

    private Long reviewId;

    private String reviewName;

    private String meetingName;

    private String meetingSubject;

    //会议状态 1待审核 2待编辑 3已发布 4已结束 5已取消 6审核未通过
    private Integer meetingStatus;

    private String remarks;

    private Date startTime;

    private Date endTime;

    //提醒类型 1 不提醒 2提前15分钟 3提前30分钟 4提前1小时
    private Integer remindType;

    private String processInstanceId;

    private String comment;

    private Date createTime;

    private Long createUserId;

    private String createUser;

    private Date updateTime;

    private Long updateUserId;

    private String updateUser;

    // 会议议程
    private List<MeetingAgenda> agendaList;

    // 会议附件
    private List<MeetingAttachment> attachmentList;
}