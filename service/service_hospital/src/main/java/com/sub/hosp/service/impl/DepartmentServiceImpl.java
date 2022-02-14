package com.sub.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sub.hosp.repository.DepartmentRepository;
import com.sub.hosp.service.DepartmentService;
import com.sub.model.hosp.Department;
import com.sub.vo.hosp.DepartmentQueryVo;
import com.sub.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 所有科室(树状结构)
     *
     * @param hospcode 科室代码
     * @return List<DepartmentVo>
     */
    @Override
    public List<DepartmentVo> findDeptTree(String hospcode) {
        List<DepartmentVo> list = new ArrayList<>();
        // 封装查询条件
        Department department = new Department();
        department.setHoscode(hospcode);
        Example<Department> of = Example.of(department);
        // 查询所有科室列表
        List<Department> all = departmentRepository.findAll(of);
        // 根据大科室bigcode分组,获得每个大科室下的子科室
        Map<String, List<Department>> collect = all.stream().collect(Collectors.groupingBy(Department::getBigcode));

        collect.forEach((k, v) -> {
            // 封装大科室 代码和名字
            DepartmentVo vo = new DepartmentVo();
            vo.setDepcode(k);
            vo.setDepname(v.get(0).getDepname());
            // 封装小科室
            List<DepartmentVo> children = new ArrayList<>(v.size());
            v.forEach(dept -> {
                DepartmentVo deptVo = new DepartmentVo();
                deptVo.setDepcode(dept.getDepcode());
                deptVo.setDepname(dept.getDepname());
                children.add(deptVo);
            });
            // 封装大科室下的小科室
            vo.setChildren(children);
            list.add(vo);
        });
        return list;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.findDepartmentByHoscodeAndDepcode(hoscode, depcode);
        return null != department ? department.getDepname() : null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.findDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
