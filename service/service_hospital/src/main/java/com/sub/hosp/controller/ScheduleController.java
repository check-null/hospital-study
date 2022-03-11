package com.sub.hosp.controller;

import com.sub.common.result.Result;
import com.sub.hosp.service.ScheduleService;
import com.sub.model.hosp.Schedule;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
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

    @ApiModelProperty("添加排班")
    @PostMapping("/add")
    public Result<Object> addSchedule(@RequestBody Schedule schedule) {
        Date workDate = schedule.getWorkDate();
        Date date = new DateTime(workDate).plusDays(1).toDate();
        schedule.setWorkDate(date);
        Boolean add = scheduleService.addSchedule(schedule);
        return Result.ok(add);
    }
}
