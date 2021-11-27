package com.sub.hosp.controller.api;

import com.sub.common.result.Result;
import com.sub.hosp.service.HospitalService;
import com.sub.model.hosp.Hospital;
import com.sub.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {

    @Resource
    HospitalService hospitalService;

    @ApiOperation("查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result<Page<Hospital>> findHospList(@PathVariable Integer page,
                                               @PathVariable Integer limit,
                                               HospitalQueryVo vo) {
        // p105创建
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, vo);
        return Result.ok(hospitals);
    }

    @ApiOperation("根据医院名称查询")
    @GetMapping("findByHosName/{hosname}")
    public Result<List<Hospital>> findByHosName(@PathVariable String hosname) {
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return Result.ok(list);
    }


}
