package com.sub.hosp.repository;

import com.sub.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    Schedule getByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
