package com.clubank.meeting.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.common.ApiStatusCode;
import com.clubank.meeting.common.MeetingException;
import com.clubank.meeting.common.WebSocket;
import com.clubank.meeting.entity.*;
import com.clubank.meeting.mapper.*;
import com.clubank.meeting.service.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private MeetingAgendaService meetingAgendaService;
    @Autowired
    private MeetingAttachmentService meetingAttachmentService;
    @Autowired
    private MeetingUserService meetingUserService;
    @Autowired
    private MeetingRoomService meetingRoomService;
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private MeetingUserMapper meetingUserMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private RoomUseInfoMapper roomUseInfoMapper;

    @Value("${meeting.process.definition.Key}")
    private String meetingProcessDefinitionKey;
    @Value("${meeting.process.definition.Key2}")
    private String meetingProcessDefinitionKey2;

    @Override
    @Transactional
    public ApiResult leaderStartMeetingApply(JSONObject meetingObject, Long userId) {
        // 操作 1提交发布 2转交处理
        Integer operatorType = meetingObject.getInteger("operatorType");
        if (operatorType != 1 && operatorType != 2) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), ApiStatusCode.PARRAM_ERROR.toString());
        }
        // 会议信息对象
        Meeting meeting = meetingObject.getObject("meeting", Meeting.class);
        if (meeting == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "会议信息对象不能为空");
        }
        meeting.setCreateTime(new Date());
        meeting.setCreateUserId(userId);
        meeting.setCreateUser(Optional.ofNullable(userMapper.selectByPrimaryKey(userId)).map(u -> u.getUserName()).orElse(null));
        // 校验会议议题和组织人
        if (StringUtils.isBlank(meeting.getMeetingSubject()) || meeting.getOrganizerId() == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "会议议题或组织人不能为空");
        }
        // 参会人员
        JSONArray meetingUserArray = meetingObject.getJSONArray("meetingUserList");
        // 领导提交发布
        if (operatorType == 1) {
            // 校验会议名称、会议时间、会议地点、参会人员
            if (StringUtils.isBlank(meeting.getMeetingName()) || meeting.getRoomId() == null || StringUtils.isBlank(meeting.getMeetingRoom()) || meeting.getStartTime() == null || meeting.getEndTime() == null) {
                return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "会议主题、会议时间、会议地点不能为空");
            }
            if (meetingUserArray == null || meetingUserArray.size() <= 1) {
                return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "参会人员不少于两人");
            }
        }
        User organizer = userMapper.selectByPrimaryKey(meeting.getOrganizerId());
        if (organizer == null) {
            return new ApiResult(ApiStatusCode.NOT_FOUND.getValue(), "ID为" + meeting.getOrganizerId() + "的用户不存在");
        }
        meeting.setOrganizerName(organizer.getUserName());
        // 保存会议内容到数据库
        int save = meetingMapper.insertSelective(meeting);
        if (save <= 0) {
            throw new MeetingException("保存会议信息失败");
        }
        // 保存会议相关信息
        saveMeetingInfo(meetingObject, meeting, meetingUserArray);
        // 流程启动
        if (operatorType == 1) {
            leaderCommitBySelf(meeting, userId);
        } else {
            leaderTransferToOrganizer(meeting, userId);
        }
        return new ApiResult(meeting);
    }

    // 领导提交发布会议
    private void leaderCommitBySelf(Meeting meeting, Long userId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("meetingId", meeting.getMeetingId());
        variables.put("leaderId", userId);
        variables.put("operatorType", 1);
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(meetingProcessDefinitionKey, variables); // 启动流程
        Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult(); // 根据流程实例id查询任务
        taskService.complete(task.getId());

        Execution execution = runtimeService.createExecutionQuery()//创建执行对象查询
                .processInstanceId(pi.getId())//使用流程实例ID查询
                .activityId("notifyParticipants")//当前活动id(通知参会人)
                .singleResult();
        runtimeService.signal(execution.getId());

        // 更新会议状态
        meeting.setMeetingStatus(Meeting.PUBLISHED);
        meeting.setProcessInstanceId(pi.getProcessInstanceId());
        meetingMapper.updateByPrimaryKeySelective(meeting);

        // TODO 推送参会人参会信息 meetingUserList
        List<Map<String, Object>> meetingUserList = meetingUserMapper.selectUserListByMeetingId(meeting.getMeetingId());
        List<Long> userIdList = Lists.newArrayList();
        for (Map<String, Object> meetingUser : meetingUserList) {
            userIdList.add((Long) meetingUser.get("userId"));
        }
        createMessageAndSend(meeting.getMeetingId(), meeting.getCreateUserId(), meeting.getCreateUser(), null, userIdList, Message.SYS_NOTIC, Message.INVITE);
    }

    // 领导转交会议申请到组织人
    private void leaderTransferToOrganizer(Meeting meeting, Long userId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("meetingId", meeting.getMeetingId());
        variables.put("leaderId", userId);
        variables.put("operatorType", 2);
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(meetingProcessDefinitionKey, variables); // 启动流程
        Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult(); // 根据流程实例id查询任务
        variables = new HashMap<>();
        variables.put("organizerId", meeting.getOrganizerId());
        taskService.complete(task.getId(), variables);
        // 更新会议状态
        meeting.setMeetingStatus(Meeting.TO_EDIT);
        meeting.setProcessInstanceId(pi.getProcessInstanceId());
        meetingMapper.updateByPrimaryKeySelective(meeting);

        // TODO 推送任务流传消息(领导转交组织人)
        List<Long> userIdList = Lists.newArrayList();
        userIdList.add(meeting.getOrganizerId());
        createMessageAndSend(meeting.getMeetingId(), meeting.getCreateUserId(), meeting.getCreateUser(), taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult().getId(), userIdList, Message.USER_TASK, Message.EDIT);
    }

    @Override
    public ApiResult staffStartMeetingApply(JSONObject meetingObject, Long userId) {
        // 会议信息对象
        Meeting meeting = meetingObject.getObject("meeting", Meeting.class);
        if (meeting == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "会议信息对象不能为空");
        }
        meeting.setCreateTime(new Date());
        meeting.setCreateUserId(userId);
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return new ApiResult(ApiStatusCode.NOT_FOUND.getValue(), "ID为" + userId + "的用户不存在");
        }
        meeting.setCreateUser(user.getUserName());
        User organizer = userMapper.selectByPrimaryKey(meeting.getOrganizerId());
        if (organizer == null) {
            return new ApiResult(ApiStatusCode.NOT_FOUND.getValue(), "ID为" + meeting.getOrganizerId() + "的用户不存在");
        }
        meeting.setOrganizerName(organizer.getUserName());
        // 校验会议议题和组织人
        if (StringUtils.isBlank(meeting.getMeetingSubject()) || meeting.getOrganizerId() == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "会议议题或组织人不能为空");
        }
        // 参会人员
        JSONArray meetingUserArray = meetingObject.getJSONArray("meetingUserList");
        // 校验会议名称、会议时间、会议地点、参会人员
        if (StringUtils.isBlank(meeting.getMeetingName()) || StringUtils.isBlank(meeting.getMeetingRoom()) || meeting.getStartTime() == null || meeting.getEndTime() == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "会议主题、会议时间、会议地点不能为空");
        }
        if (meetingUserArray == null || meetingUserArray.size() <= 1) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "参会人员不少于两人");
        }
        // 保存会议内容到数据库
        int save = meetingMapper.insertSelective(meeting);
        if (save <= 0) {
            throw new MeetingException("保存会议信息失败");
        }
        // 保存会议相关信息
        saveMeetingInfo(meetingObject, meeting, meetingUserArray);
        // 查询员工所在部门的领导
        Department department = departmentMapper.selectByPrimaryKey(user.getDepartmentId());
        if (department == null) {
            return new ApiResult(ApiStatusCode.NOT_FOUND.getValue(), "ID为" + user.getDepartmentId() + "的部门不存在");
        }
        User leader = userMapper.selectByPrimaryKey(department.getDepartmentLeader());
        if (leader == null) {
            return new ApiResult(ApiStatusCode.NOT_FOUND.getValue(), "ID为" + department.getDepartmentLeader() + "的领导不存在");
        }
        // 发起审批
        Map<String, Object> variables = new HashMap<>();
        variables.put("meetingId", meeting.getMeetingId());
        variables.put("organizerId", userId);
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(meetingProcessDefinitionKey2, variables); // 启动流程
        Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult(); // 根据流程实例id查询任务
        variables = new HashMap<>();
        variables.put("leaderId", leader.getUserId());
        taskService.complete(task.getId(), variables);
        // 添加审批人
        meeting.setReviewId(leader.getUserId());
        meeting.setReviewName(leader.getUserName());
        // 更新会议状态
        meeting.setMeetingStatus(Meeting.TO_REVIEW);
        meeting.setProcessInstanceId(pi.getProcessInstanceId());
        meetingMapper.updateByPrimaryKeySelective(meeting);

        // TODO 推送任务流传消息(员工发起申请)
        List<Long> userIdList = Lists.newArrayList();
        userIdList.add(meeting.getReviewId());
        createMessageAndSend(meeting.getMeetingId(), meeting.getCreateUserId(), meeting.getCreateUser(), taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult().getId(), userIdList, Message.USER_TASK, Message.REVIEW);

        return new ApiResult(meetingMapper.selectByPrimaryKey(meeting.getMeetingId()));
    }

    private void saveMeetingInfo(JSONObject meetingObject, Meeting meeting, JSONArray meetingUserArray) {
        Long meetingId = meeting.getMeetingId();
        JSONArray agendaArray = meetingObject.getJSONArray("agendaList");
        if (agendaArray != null && agendaArray.size() > 0) {
            List<MeetingAgenda> meetingAgendaList = JSONObject.parseArray(agendaArray.toJSONString(), MeetingAgenda.class);
            for (MeetingAgenda meetingAgenda : meetingAgendaList) {
                meetingAgenda.setMeetingId(meetingId);
            }
            int insertBatch = meetingAgendaService.insertBatch(meetingAgendaList);
            if (insertBatch <= 0) {
                throw new MeetingException("保存会议议程失败");
            }
        }
        // 添加会议附件
        JSONArray attachmentArray = meetingObject.getJSONArray("attachmentList");
        if (attachmentArray != null && attachmentArray.size() > 0) {
            List<MeetingAttachment> attachmentList = JSONObject.parseArray(attachmentArray.toJSONString(), MeetingAttachment.class);
            for (MeetingAttachment meetingAttachment : attachmentList) {
                meetingAttachment.setMeetingId(meetingId);
            }
            int insertBatch = meetingAttachmentService.insertBatch(attachmentList);
            if (insertBatch <= 0) {
                throw new MeetingException("保存会议附件失败");
            }
        }
        // 添加参会人员
        if (meetingUserArray != null && meetingUserArray.size() > 0) {
            List<MeetingUser> meetingUserList = JSONObject.parseArray(meetingUserArray.toJSONString(), MeetingUser.class).stream().distinct().collect(Collectors.toList());
            for (MeetingUser meetingUser : meetingUserList) {
                meetingUser.setMeetingId(meetingId);
            }
            int insertBatch = meetingUserService.insertBatch(meetingUserList);
            if (insertBatch <= 0) {
                throw new MeetingException("保存参会人员失败");
            }
        }
        if (meeting.getRoomId() != null || meeting.getStartTime() != null || meeting.getEndTime() != null) {
            // 判断当前会议室在本次会议开始结束期间是否已经被占用
            List<RoomUseInfo> roomUseInfoList = roomUseInfoMapper.selectConflictList(meeting.getRoomId(), Optional.ofNullable(meeting.getStartTime()).toString(), Optional.ofNullable(meeting.getEndTime()).toString());
            if (!CollectionUtils.isEmpty(roomUseInfoList)) {
                throw new MeetingException("本次会议预定的会议室在该时段已被占用，请重新选择会议室活时间");
            }
            // 添加会议室使用信息
            RoomUseInfo roomUseInfo = RoomUseInfo.builder().roomId(meeting.getRoomId()).meetingId(meetingId)
                    .startTime(meeting.getStartTime()).endTime(meeting.getEndTime()).status(1).build();
            int saveMeetingRoomUseInfo = meetingRoomService.saveMeetingRoomUseInfo(roomUseInfo);
            if (saveMeetingRoomUseInfo <= 0) {
                throw new MeetingException("保存会议室预定信息失败");
            }
        }
    }

    @Override
    @Transactional
    public ApiResult organizerUpdateMeetingAndCommit(JSONObject meetingObject, Long userId) {
        String taskId = meetingObject.getString("taskId");
        if (StringUtils.isBlank(taskId)) {
            throw new MeetingException("任务ID不能为空");
        }
        Meeting meeting = meetingObject.getObject("meeting", Meeting.class);
        if (meeting.getMeetingId() == null || StringUtils.isBlank(meeting.getProcessInstanceId())) {
            throw new MeetingException("会议ID或流程实例ID为空");
        }
        meeting.setOrganizerName(Optional.ofNullable(userMapper.selectByPrimaryKey(meeting.getOrganizerId())).map(u -> u.getUserName()).orElse(null));
        meetingMapper.updateByPrimaryKeySelective(meeting);
        // 附件全部删除后全量更新插入
        meetingAttachmentService.deleteByMeetingId(meeting.getMeetingId());
        JSONArray attachmentArray = meetingObject.getJSONArray("attachmentList");
        if (attachmentArray != null && attachmentArray.size() > 0) {
            List<MeetingAttachment> attachmentList = JSONObject.parseArray(attachmentArray.toJSONString(), MeetingAttachment.class);
            for (MeetingAttachment meetingAttachment : attachmentList) {
                meetingAttachment.setMeetingId(meeting.getMeetingId());
            }
            int insertBatch = meetingAttachmentService.insertBatch(attachmentList);
            if (insertBatch <= 0) {
                throw new MeetingException("更新会议信息失败");
            }
        }
        // 议程全部删除后全量更新插入
        meetingAgendaService.deleteByMeetingId(meeting.getMeetingId());
        JSONArray agendaArray = meetingObject.getJSONArray("agendaList");
        if (agendaArray != null && agendaArray.size() > 0) {
            List<MeetingAgenda> meetingAgendaList = JSONObject.parseArray(agendaArray.toJSONString(), MeetingAgenda.class);
            for (MeetingAgenda meetingAgenda : meetingAgendaList) {
                meetingAgenda.setMeetingId(meeting.getMeetingId());
            }
            int insertBatch = meetingAgendaService.insertBatch(meetingAgendaList);
            if (insertBatch <= 0) {
                throw new MeetingException("更新会议信息失败");
            }
        }
        // 参会人员全部删除后全量更新插入
        meetingUserService.deleteByMeetingId(meeting.getMeetingId());
        JSONArray meetingUserArray = meetingObject.getJSONArray("meetingUserList");
        if (meetingUserArray != null && meetingUserArray.size() > 0) {
            List<MeetingUser> meetingUserList = JSONObject.parseArray(meetingUserArray.toJSONString(), MeetingUser.class).stream().distinct().collect(Collectors.toList());
            for (MeetingUser meetingUser : meetingUserList) {
                meetingUser.setMeetingId(meeting.getMeetingId());
            }
            int insertBatch = meetingUserService.insertBatch(meetingUserList);
            if (insertBatch <= 0) {
                throw new MeetingException("更新会议信息失败");
            }
        }
        // 会议室使用信息删除后新增记录
        meetingRoomService.deleteByMeetingId(meeting.getMeetingId());
        // 判断当前会议室在本次会议开始结束期间是否已经被占用
        List<RoomUseInfo> roomUseInfoList = roomUseInfoMapper.selectConflictList(meeting.getRoomId(), meeting.getStartTime().toString(), meeting.getEndTime().toString());
        if (!CollectionUtils.isEmpty(roomUseInfoList)) {
            throw new MeetingException("本次会议预定的会议室在该时段已被占用，请重新选择会议室活时间");
        }
        RoomUseInfo roomUseInfo = RoomUseInfo.builder().roomId(meeting.getRoomId()).meetingId(meeting.getMeetingId())
                .startTime(meeting.getStartTime()).endTime(meeting.getEndTime()).status(1).build();
        int saveMeetingRoomUseInfo = meetingRoomService.saveMeetingRoomUseInfo(roomUseInfo);
        if (saveMeetingRoomUseInfo <= 0) {
            throw new MeetingException("更新会议信息失败");
        }
        // 提交审批
        Map<String, Object> variables = new HashMap<>();
        Long leaderId = (Long) taskService.getVariable(taskId, "leaderId");
        variables.put("leaderId", leaderId);
        taskService.complete(taskId, variables);
        // 添加会议审批人
        meeting.setReviewId(leaderId);
        meeting.setReviewName(userMapper.selectByPrimaryKey(leaderId).getUserName());
        // 更新会议状态
        meeting.setMeetingStatus(Meeting.TO_REVIEW);
        // 会议编辑人
        meeting.setUpdateTime(new Date());
        meeting.setUpdateUserId(userId);
        meeting.setUpdateUser(Optional.ofNullable(userMapper.selectByPrimaryKey(userId)).map(u -> u.getUserName()).orElse(null));
        meetingMapper.updateByPrimaryKeySelective(meeting);

        // TODO 推送任务流传消息(组织人编辑并提交审批)
        List<Long> userIdList = Lists.newArrayList();
        userIdList.add(meeting.getReviewId());
        createMessageAndSend(meeting.getMeetingId(), meeting.getOrganizerId(), meeting.getOrganizerName(), taskService.createTaskQuery().processInstanceId(meeting.getProcessInstanceId()).singleResult().getId(), userIdList, Message.USER_TASK, Message.REVIEW);

        return new ApiResult(meetingMapper.selectByPrimaryKey(meeting.getMeetingId()));
    }

    @Override
    @Transactional
    public ApiResult leaderApproval(JSONObject param, Long userId) {
        String taskId = param.getString("taskId");
        String processInstanceId = param.getString("processInstanceId");
        String comment = param.getString("comment");
        Integer approveStatus = param.getInteger("approveStatus");
        Long meetingId = param.getLongValue("meetingId");
        Meeting meeting = meetingMapper.selectByPrimaryKey(meetingId);
        if (meeting == null) {
            return new ApiResult(ApiStatusCode.NOT_FOUND.getValue(), "ID为" + meetingId + "的会议不存在");
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("organizerId", taskService.getVariable(taskId, "organizerId"));
        variables.put("approveStatus", approveStatus);
        taskService.addComment(taskId, processInstanceId, comment); // 添加批注信息
        taskService.complete(taskId, variables);
        // 审批通过
        if (approveStatus == 1) {
            meeting.setMeetingStatus(Meeting.PUBLISHED); // 已发布

            // TODO 推送参会人参会信息 meetingUserList
            List<Map<String, Object>> meetingUserList = meetingUserMapper.selectUserListByMeetingId(meeting.getMeetingId());
            List<Long> userIdList = Lists.newArrayList();
            for (Map<String, Object> meetingUser : meetingUserList) {
                userIdList.add((Long) meetingUser.get("userId"));
            }
            createMessageAndSend(meeting.getMeetingId(), meeting.getCreateUserId(), meeting.getCreateUser(), null, userIdList, Message.SYS_NOTIC, Message.INVITE);
        }
        // 退回修改
        else if (approveStatus == 2) {
            meeting.setMeetingStatus(Meeting.TO_EDIT); // 待编辑

            // TODO 推送组织人
            List<Long> userIdList = Lists.newArrayList();
            userIdList.add(meeting.getOrganizerId());
            createMessageAndSend(meeting.getMeetingId(), meeting.getReviewId(), meeting.getReviewName(), taskService.createTaskQuery().processInstanceId(meeting.getProcessInstanceId()).singleResult().getId(), userIdList, Message.USER_TASK, Message.EDIT);
        }
        // 否决申请
        else if (approveStatus == 3) {
            meeting.setMeetingStatus(Meeting.REJECT); // 不通过

            // TODO 推送申请人
            List<Long> userIdList = Lists.newArrayList();
            userIdList.add(meeting.getOrganizerId());
            userIdList.add(meeting.getCreateUserId());
            createMessageAndSend(meeting.getMeetingId(), meeting.getCreateUserId(), meeting.getCreateUser(), null, userIdList, Message.USER_TASK, Message.REJECT);

            // 更新会议使用状态
            roomUseInfoMapper.updateByMeetingId(meetingId, 2);
        } else {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "审批操作参数异常");
        }
        meeting.setComment(comment);
        meetingMapper.updateByPrimaryKeySelective(meeting);
        return new ApiResult(meetingMapper.selectByPrimaryKey(meeting.getMeetingId()));
    }

    private void createMessageAndSend(Long meetingId, Long createUserId, String createUserName, String taskId, List<Long> userIdList, Integer messageType, String messageTitle) {
        try {
            Message message = new Message();
            message.setMeetingId(meetingId);
            message.setMessageTitle(messageTitle);
            message.setMessageType(messageType);
            message.setTaskId(taskId);
            message.setCreateTime(new Date());
            message.setCreateUserId(createUserId);
            message.setCreateUserName(createUserName);
            messageMapper.insertSelective(message);
            userIdList = userIdList.stream().distinct().collect(Collectors.toList());
            for (Long userId : userIdList) {
                userMessageMapper.insertSelective(UserMessage.builder().messageId(message.getMessageId()).userId(userId).build());
            }
            // 消息推送
            webSocket.sendMessageToSpecial(JSON.toJSONString(message, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue), userIdList);
        } catch (Exception e) {
            log.error("消息推送异常", e);
        }
    }

    @Override
    public Meeting selectWithDetailsByMeetingId(Long meetingId) {
        return meetingMapper.selectWithDetailsByPrimaryKey(meetingId);
    }

    @Override
    public List<Meeting> findMeetingListByCreateUserId(Long userId) {
        return meetingMapper.selectMeetingListByCreateUserId(userId);
    }

    @Override
    public List<Meeting> findMeetingListByOrganizerId(Long userId) {
        return meetingMapper.selectMeetingListByOrganizerId(userId);
    }

    @Override
    public List<Meeting> findHandledMeetingList(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return Lists.newArrayList();
        }
        return meetingMapper.selectHandledMeetingListByUserId(userId, user.getUserRole());
    }

    @Override
    public List<Map<String, Object>> findUserListByMeetingId(Long meetingId) {
        return meetingUserMapper.selectUserListByMeetingId(meetingId);
    }

    @Override
    public ApiResult cancelMeetingApply(Long meetingId, Long userId) {
        Meeting meeting = meetingMapper.selectByPrimaryKey(meetingId);
        if (meeting == null) {
            return new ApiResult(ApiStatusCode.DATA_NOT_EXIST.getValue(), "ID为" + meetingId + "的会议信息不存在");
        }
        if (meeting.getMeetingStatus() != Meeting.TO_REVIEW) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "取消申请只可取消待审核会议");
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return new ApiResult(ApiStatusCode.DATA_NOT_EXIST.getValue(), "ID为" + userId + "的用户不存在");
        }
        if (StringUtils.isBlank(meeting.getProcessInstanceId())) {
            throw new MeetingException("流程实例不存在");
        }
        runtimeService.deleteProcessInstance(meeting.getProcessInstanceId(), "取消会议申请"); //删除流程
        // 会议状态改为已取消
        meeting.setMeetingStatus(Meeting.CANCELED);
        meeting.setUpdateTime(new Date());
        meeting.setUpdateUserId(userId);
        meeting.setUpdateUser(user.getUserName());
        int update = meetingMapper.updateByPrimaryKeySelective(meeting);
        if (update <= 0) {
            throw new MeetingException("会议申请取消失败");
        }
        // 更新会议使用状态
        roomUseInfoMapper.updateByMeetingId(meetingId, 2);
        return new ApiResult();
    }

    @Override
    public ApiResult cancelPublishedMeeting(Long meetingId, Long userId) {
        Meeting meeting = meetingMapper.selectByPrimaryKey(meetingId);
        if (meeting == null) {
            return new ApiResult(ApiStatusCode.DATA_NOT_EXIST.getValue(), "ID为" + meetingId + "的会议信息不存在");
        }
        if (meeting.getMeetingStatus() != Meeting.PUBLISHED) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "取消会议只可取消已发布会议");
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return new ApiResult(ApiStatusCode.DATA_NOT_EXIST.getValue(), "ID为" + userId + "的用户不存在");
        }
        // 会议状态改为已取消
        meeting.setMeetingStatus(Meeting.CANCELED);
        meeting.setUpdateTime(new Date());
        meeting.setUpdateUserId(userId);
        meeting.setUpdateUser(user.getUserName());
        int update = meetingMapper.updateByPrimaryKeySelective(meeting);
        if (update <= 0) {
            throw new MeetingException("会议取消失败");
        }
        // 更新会议使用状态
        roomUseInfoMapper.updateByMeetingId(meetingId, 2);

        // TODO 取消会议通知参会人
        List<Map<String, Object>> meetingUserList = meetingUserMapper.selectUserListByMeetingId(meeting.getMeetingId());
        List<Long> userIdList = Lists.newArrayList();
        for (Map<String, Object> meetingUser : meetingUserList) {
            userIdList.add((Long) meetingUser.get("userId"));
        }
        createMessageAndSend(meeting.getMeetingId(), user.getUserId(), user.getUserName(), null, userIdList, Message.SYS_NOTIC, Message.CANCEL);
        return new ApiResult();
    }

    @Override
    public List<Meeting> findApprovedMeetingByReviewId(Long userId, Integer type) {
        // 已批准
        if (type == 1) {
            return meetingMapper.selectApprovedMeetingByReviewId(userId, Meeting.PUBLISHED);
        }
        // 审批不通过
        else if (type == 2) {
            return meetingMapper.selectApprovedMeetingByReviewId(userId, Meeting.REJECT);
        }
        return Lists.newArrayList();
    }

    @Override
    public ApiResult confirmWhetherAttend(MeetingUser meetingUser, Long userId) {
        if (meetingUser == null || meetingUser.getMeetingId() == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "参数为空");
        }
        meetingUser.setUserId(userId);
        // 确认参会
        if (meetingUser.getConfirmStatus() == 2) {
            meetingUserMapper.updateByPrimaryKeySelective(meetingUser);
        }
        // 放弃参会
        else if (meetingUser.getConfirmStatus() == 3) {
            meetingUserMapper.updateByPrimaryKeySelective(meetingUser);
        }
        // 参数错误
        else {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "参数错误");
        }
        return new ApiResult();
    }

    @Override
    public List<Meeting> findAttendedMeetingList(Long userId) {
        return meetingMapper.selectAttendedMeetingList(userId);
    }

    @Override
    public List<Meeting> findGiveUpMeetingList(Long userId) {
        return meetingMapper.selectGiveUpMeetingList(userId);
    }

    @Override
    public List<Meeting> findToAttendMeetingList(Long userId) {
        return meetingMapper.selectToAttendMeetingList(userId);
    }

    @Override
    public ApiResult remindLeaderReview(JSONObject param, Long userId) {
        Long meetingId = param.getLongValue("meetingId");
        Long reviewId = param.getLongValue("reviewId");
        Meeting meeting = meetingMapper.selectByPrimaryKey(meetingId);
        if (meeting == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "ID为:" + meeting + "的会议不存在");
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "ID为:" + userId + "的用户不存在");
        }
        // TODO 推送催审消息给审批人
        Message message = messageMapper.selectByMeetingIdAndUserId(meetingId, reviewId);
        List<Long> userIdList = Lists.newArrayList();
        userIdList.add(reviewId);
        if (message != null) {
            message.setCreateTime(new Date());
            messageMapper.updateByPrimaryKeySelective(message);
            userMessageMapper.updateByMessageIdAndUserId(UserMessage.builder().messageId(message.getMessageId()).userId(reviewId).isRead(false).build());
            webSocket.sendMessageToSpecial(JSON.toJSONString(message, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue), userIdList);
        } else {
            createMessageAndSend(meeting.getMeetingId(), user.getUserId(), user.getUserName(), taskService.createTaskQuery().processInstanceId(meeting.getProcessInstanceId()).singleResult().getId(), userIdList, Message.REMINDER, Message.REVIEW);
        }
        return new ApiResult();
    }

    @Override
    public void remindUserToAttendMeeting() {
        // 查询当前需要提醒参会人的会议
        List<Meeting> meetingList = meetingMapper.selectAboutToStartMeeting();
        if (!CollectionUtils.isEmpty(meetingList)) {
            Multimap<String, Meeting> meetingMultimap = ArrayListMultimap.create();
            for (Meeting meeting : meetingList) {
                meetingMultimap.put("type" + meeting.getRemindType(), meeting);
            }
            // 按提醒类型分类
            // 提醒类型 1 不提醒 2提前15分钟 3提前30分钟 4提前1小时
            List<Meeting> type2 = (List<Meeting>) meetingMultimap.get("type2");
            if (!CollectionUtils.isEmpty(type2)) {
                for (Meeting meeting : type2) {
                    // TODO 推送提醒参会消息
                    remindMeetingUser(meeting);
                }
            }
            List<Meeting> type3 = (List<Meeting>) meetingMultimap.get("type3");
            if (!CollectionUtils.isEmpty(type3)) {
                for (Meeting meeting : type3) {
                    // TODO 推送提醒参会消息
                    remindMeetingUser(meeting);
                }
            }
            List<Meeting> type4 = (List<Meeting>) meetingMultimap.get("type4");
            if (!CollectionUtils.isEmpty(type4)) {
                for (Meeting meeting : type4) {
                    // TODO 推送提醒参会消息
                    remindMeetingUser(meeting);
                }
            }
        }
    }

    private void remindMeetingUser(Meeting meeting) {
        List<Map<String, Object>> attendeeList = meetingUserMapper.selectAttendeeListByMeetingId(meeting.getMeetingId());
        if (!CollectionUtils.isEmpty(attendeeList)) {
            List<Long> userIdList = Lists.newArrayList();
            for (Map<String, Object> meetingUser : attendeeList) {
                userIdList.add((Long) meetingUser.get("userId"));
            }
            createMessageAndSend(meeting.getMeetingId(), meeting.getCreateUserId(), meeting.getCreateUser(), null, userIdList, Message.SYS_NOTIC, Message.REMIND);
            log.info("定时任务：提醒参会人员参加会议,{}", userIdList.size());
        }
    }
}
