package com.sub.hosp.controller;

import com.sub.common.result.Result;
import com.sub.hosp.service.HospitalService;
import com.sub.model.hosp.Hospital;
import com.sub.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Europa
 */
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Resource
    private HospitalService hospitalService;

    @GetMapping("/list/{page}/{limit}")
    public Result<Page<Hospital>> listHosp(@PathVariable Integer page,
                                           @PathVariable Integer limit,
                                           HospitalQueryVo queryVo) {
        Page<Hospital> pageModel =  hospitalService.selectHospPage(page, limit, queryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation("更新医院上线状态")
    @GetMapping("/updateHospStatus/{id}/{status}")
    public Result<Boolean> updateHospStatus(@PathVariable String id,
                                            @PathVariable Integer status) {
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }


}
