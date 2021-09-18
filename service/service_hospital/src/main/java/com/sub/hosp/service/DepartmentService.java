package com.sub.hosp.service;

import com.sub.model.hosp.Department;
import com.sub.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> paramMap);


    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo queryVo);

    void remove(String hoscode, String depcode);
}