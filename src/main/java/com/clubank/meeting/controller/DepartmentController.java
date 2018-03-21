package com.clubank.meeting.controller;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.Department;
import com.clubank.meeting.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 部门管理
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 查询所有顶级部门
     *
     * @return
     */
    @GetMapping("/root/list")
    public ApiResult findRootDepartmentList() {
        return departmentService.findRootDepartmentList();
    }

    /**
     * 根据parentId查询子部门列表
     *
     * @return
     */
    @GetMapping("/list/{parentId}")
    public ApiResult findDepartmentListByParentId(@PathVariable("parentId") Long parentId) {
        return departmentService.findDepartmentListByParentId(parentId);
    }

    /**
     * 查询部门树
     *
     * @return
     */
    @GetMapping("/tree/list")
    public ApiResult departmentTree() {
        return departmentService.departmentTree();
    }

    /**
     * 添加部门
     *
     * @param department
     * @return
     */
    @PostMapping("/save")
    public ApiResult saveDepartment(@RequestBody Department department) {
        return departmentService.save(department);
    }
}
