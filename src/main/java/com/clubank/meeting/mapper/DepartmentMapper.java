package com.clubank.meeting.mapper;

import com.clubank.meeting.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentMapper {
    int deleteByPrimaryKey(Long departmentId);

    int insert(Department record);

    int insertSelective(Department record);

    Department selectByPrimaryKey(Long departmentId);

    int updateByPrimaryKeySelective(Department record);

    int updateByPrimaryKey(Department record);

    List<Department> selectRootDepartmentList();

    List<Department> selectByParentId(Long parentId);

    List<Department> selectAllDepartmentList();

    int countByNameAndParentId(@Param("parentId") Long parentId, @Param("departmentName") String departmentName, @Param("departmentId") Long departmentId);
}