package com.sub.hosp.controller;

import com.sub.common.result.Result;
import com.sub.hosp.service.DepartmentService;
import com.sub.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @ApiOperation("查询医院所有科室")
    @GetMapping("getDeptList/{hospcode}")
    public Result<List<DepartmentVo>> getDeptList(@PathVariable String hospcode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hospcode);
        return Result.ok(list);
    }
}
