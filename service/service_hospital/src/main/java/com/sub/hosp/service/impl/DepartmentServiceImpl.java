package com.sub.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sub.hosp.repository.DepartmentRepository;
import com.sub.hosp.service.DepartmentService;
import com.sub.model.hosp.Department;
import com.sub.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author Europa
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentRepository departmentRepository;


    @Override
    public void save(Map<String, Object> paramMap) {
        String s = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(s, Department.class);

        // 医院编号和科室编号
        Department dept = departmentRepository.getByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        if (dept != null) {
            dept.setUpdateTime(new Date());
            dept.setIsDeleted(0);
            departmentRepository.save(dept);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo queryVo) {
        Pageable of = PageRequest.of(page - 1, limit);
        Department department = new Department();
        BeanUtils.copyProperties(queryVo, department);
        department.setIsDeleted(0);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department, matcher);

        return departmentRepository.findAll(example, of);
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }
}
