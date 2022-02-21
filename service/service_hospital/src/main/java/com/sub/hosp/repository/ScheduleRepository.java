package com.sub.hosp.repository;

import com.sub.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    Schedule getByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);

    /**
     * @param scheduleId id
     * @return Schedule
     */
    Schedule getById(String scheduleId);
}
