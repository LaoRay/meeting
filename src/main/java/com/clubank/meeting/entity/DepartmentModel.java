package com.clubank.meeting.entity;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentModel extends Department {

    private List<DepartmentModel> departmentModelList = Lists.newArrayList();

    public static DepartmentModel adapt(Department department) {
        DepartmentModel departmentModel = new DepartmentModel();
        BeanUtils.copyProperties(department, departmentModel);
        return departmentModel;
    }
}
