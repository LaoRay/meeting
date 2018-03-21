package com.clubank.meeting.service;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.entity.Department;

public interface DepartmentService {
    ApiResult findRootDepartmentList();

    ApiResult findDepartmentListByParentId(Long parentId);

    ApiResult departmentTree();

    ApiResult save(Department department);
}
