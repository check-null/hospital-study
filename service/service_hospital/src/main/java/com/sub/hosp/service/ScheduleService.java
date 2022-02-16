package com.sub.hosp.service;

import com.sub.model.hosp.Schedule;
import com.sub.vo.hosp.ScheduleOrderVo;
import com.sub.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo queryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getRuleSchedule(Integer page, Integer limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Schedule getByScheduleId(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);
}
