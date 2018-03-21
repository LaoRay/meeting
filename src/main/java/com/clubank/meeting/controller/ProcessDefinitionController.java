package com.clubank.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.clubank.meeting.common.ApiResult;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程定义管理
 */
@RestController
@RequestMapping("/processDefinition")
public class ProcessDefinitionController {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 最新版流程定义查询
     *
     * @return
     */
    @GetMapping("/list")
    public ApiResult list() {
        List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery() // 创建流程定义查询
                .orderByProcessDefinitionKey().desc() //根据流程定义key降序排列
                .list();
        // 查询最新版的流程定义
        Map<String, ProcessDefinition> map = new LinkedHashMap<>();
        if (pdList != null && pdList.size() > 0) {
            for (ProcessDefinition pd : pdList) {
                map.put(pd.getKey(), pd);
            }
        }
        List<ProcessDefinition> list = new ArrayList<>(map.values());
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add("identityLinks");
        filter.getExcludes().add("processDefinition");
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list, filter, SerializerFeature.WriteDateUseDateFormat));
        return new ApiResult(jsonArray);
    }
}
