package com.clubank.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.clubank.meeting.common.ApiResult;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程实例管理
 */
@RestController
@RequestMapping("/processInstance")
public class ProcessInstanceController {

    @Autowired
    private HistoryService historyService;

    /**
     * 根据任务id查询该流程实例的执行过程
     *
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping("/listAction")
    public ApiResult listAction(@RequestBody JSONObject param) throws Exception {
        String taskId = param.getString("taskId");
        if (StringUtils.isBlank(taskId)) {
            return null;
        }
        HistoricTaskInstance hti = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        String processInstanceId = hti.getProcessInstanceId(); // 获取流程实例id
        List<HistoricActivityInstance> hciList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).list();
        JSONArray jsonArray = JSONArray
                .parseArray(JSON.toJSONString(hciList, SerializerFeature.WriteDateUseDateFormat));
        return new ApiResult(jsonArray);
    }
}
