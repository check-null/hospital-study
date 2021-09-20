package com.sub.hosp.service;

import com.sub.model.hosp.Schedule;
import com.sub.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo queryVo);

    void remove(String hoscode, String hosScheduleId);
}
