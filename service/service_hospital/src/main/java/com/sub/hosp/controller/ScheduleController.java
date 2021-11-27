package com.sub.hosp.controller;

import com.sub.common.result.Result;
import com.sub.hosp.service.ScheduleService;
import com.sub.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Resource
    ScheduleService scheduleService;

    @ApiOperation("查询排班规则顺序")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result<Map<String, Object>> getScheduleRule(@PathVariable Integer page,
                                                       @PathVariable Integer limit,
                                                       @PathVariable String hoscode,
                                                       @PathVariable String depcode) {

        Map<String, Object> map = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }

    @ApiOperation("查询排班信息详情")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result<List<Schedule>> getScheduleDetail(@PathVariable String hoscode,
                                                    @PathVariable String depcode,
                                                    @PathVariable String workDate) {

        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(list);
    }
}
