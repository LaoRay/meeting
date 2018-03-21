package com.clubank.meeting.service.impl;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.common.ApiStatusCode;
import com.clubank.meeting.common.MeetingException;
import com.clubank.meeting.entity.Department;
import com.clubank.meeting.entity.DepartmentModel;
import com.clubank.meeting.mapper.DepartmentMapper;
import com.clubank.meeting.service.DepartmentService;
import com.clubank.meeting.utils.DepartmentLevelUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public ApiResult findRootDepartmentList() {
        List<Department> rootDepartmentList = departmentMapper.selectRootDepartmentList();
        return new ApiResult(rootDepartmentList);
    }

    @Override
    public ApiResult findDepartmentListByParentId(Long parentId) {
        List<Department> departmentList = departmentMapper.selectByParentId(parentId);
        return new ApiResult(departmentList);
    }

    /**
     * 部门树
     *
     * @return
     */
    @Override
    public ApiResult departmentTree() {
        List<Department> departmentList = departmentMapper.selectAllDepartmentList();
        List<DepartmentModel> departmentModelList = Lists.newArrayList();
        for (Department department : departmentList) {
            DepartmentModel departmentModel = DepartmentModel.adapt(department);
            departmentModelList.add(departmentModel);
        }
        return new ApiResult(departmentListToTree(departmentModelList));
    }

    private List<DepartmentModel> departmentListToTree(List<DepartmentModel> departmentModelList) {
        if (CollectionUtils.isEmpty(departmentModelList)) {
            return Lists.newArrayList();
        }
        Multimap<String, DepartmentModel> modelMultimap = ArrayListMultimap.create();
        List<DepartmentModel> rootList = Lists.newArrayList();

        for (DepartmentModel departmentModel : departmentModelList) {
            modelMultimap.put(departmentModel.getDepartmentLevel(), departmentModel);
            if (DepartmentLevelUtil.ROOT.equals(departmentModel.getDepartmentLevel())) {
                rootList.add(departmentModel);
            }
        }
        Collections.sort(rootList, Comparator.comparing(DepartmentModel::getDepartmentSort));

        transformDepartmentTree(rootList, DepartmentLevelUtil.ROOT, modelMultimap);
        return rootList;
    }

    private void transformDepartmentTree(List<DepartmentModel> rootList, String level, Multimap<String, DepartmentModel> modelMultimap) {
        // 遍历该层的每个元素
        for (DepartmentModel departmentModel : rootList) {
            String nextLevel = DepartmentLevelUtil.calculateLevel(level, departmentModel.getDepartmentId());
            List<DepartmentModel> tempList = (List<DepartmentModel>) modelMultimap.get(nextLevel);
            if (!CollectionUtils.isEmpty(tempList)) {
                // 排序
                Collections.sort(tempList, Comparator.comparing(DepartmentModel::getDepartmentSort));
                // 设置下一层部门
                departmentModel.setDepartmentModelList(tempList);
                // 递归到下一层
                transformDepartmentTree(tempList, nextLevel, modelMultimap);
            }
        }
    }

    @Override
    public ApiResult save(Department department) {
        if (department == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "部门对象为空");
        }
        if (checkExist(department.getParentId(), department.getDepartmentName(), department.getDepartmentId())) {
            return new ApiResult(ApiStatusCode.DATA_EXIST.getValue(), "同一层级下存在相同名称的部门");
        }
        String level = DepartmentLevelUtil.calculateLevel(getLevel(department.getParentId()), department.getParentId());
        department.setDepartmentLevel(level);
        int insert = departmentMapper.insertSelective(department);
        if (insert <= 0) {
            throw new MeetingException("保存部门信息失败");
        }
        return new ApiResult(department.getDepartmentId());
    }

    private String getLevel(Long departmentId) {
        Department department = departmentMapper.selectByPrimaryKey(departmentId);
        if (department == null) {
            return null;
        }
        return department.getDepartmentLevel();
    }

    private boolean checkExist(Long parentId, String departmentName, Long departmentId) {
        return departmentMapper.countByNameAndParentId(parentId, departmentName, departmentId) > 0;
    }
}
