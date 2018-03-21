package com.clubank.meeting.common;

import com.clubank.meeting.mapper.MeetingMapper;
import com.clubank.meeting.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Async
@Slf4j
public class Scheduler {

    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private MeetingService meetingService;

    @Scheduled(cron = "0 0,15,30,45 6-20 * * ?")
    public void handleFinishMeetingStatus() {
        //会议结束后将状态置为已结束
        int result = meetingMapper.updateFinishMeetingStatus();
        log.info("定时任务：处理{}条已结束的会议的状态", result);
    }

    @Scheduled(cron = "0 0,15,30,45 6-20 * * ?")
    public void remindUserToAttendMeeting() {
        //提醒参会人员参加会议
        meetingService.remindUserToAttendMeeting();
    }
}
