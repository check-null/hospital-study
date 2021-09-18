package com.sub.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sub.hosp.repository.ScheduleRepository;
import com.sub.hosp.service.ScheduleService;
import com.sub.model.hosp.Schedule;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author Europa
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    ScheduleRepository scheduleRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        String s = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(s, Schedule.class);

        // 医院编号 和 排班编号
        Schedule exist = scheduleRepository.getByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (exist != null) {
            exist.setUpdateTime(new Date());
            exist.setIsDeleted(0);
            exist.setStatus(1);
            scheduleRepository.save(exist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }
}