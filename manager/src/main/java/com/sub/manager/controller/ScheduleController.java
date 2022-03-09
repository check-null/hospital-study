package com.sub.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.sub.manager.model.Schedule;
import com.sub.manager.service.ScheduleService;
import com.sub.manager.util.HttpRequestHelper;
import com.sub.manager.util.Result;
import com.sub.manager.util.ResultCodeEnum;
import com.sub.manager.util.YyghException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("schedule")
public class ScheduleController {

    @Resource
    ScheduleService scheduleService;

    @PostMapping("/addSchedule")
    public Result<Object> addSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String jsonString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);
        scheduleService.save(schedule);
        return Result.ok(schedule.getId());
    }
}
