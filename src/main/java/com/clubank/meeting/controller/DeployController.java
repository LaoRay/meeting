package com.clubank.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.clubank.meeting.common.ApiResult;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程部署管理
 */
@RestController
@RequestMapping("/deploy")
public class DeployController {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 查询流程部署
     *
     * @return
     */
    @GetMapping("/list")
    public ApiResult list() {
        List<Deployment> deploymentList = repositoryService.createDeploymentQuery() // 创建流程部署查询
                .orderByDeploymenTime().desc() // 根据部署时间降序排列
                .list();
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add("resources");
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(deploymentList, filter, SerializerFeature.WriteDateUseDateFormat));
        return new ApiResult(jsonArray);
    }
}
