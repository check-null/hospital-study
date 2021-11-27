package com.sub.hosp.repository;

import com.sub.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    /**
     * 通过hoscode查询医院
     *
     * @param hoscode hosp code
     * @return com.sub.model.hosp.Hospital
     */
    Hospital getHospitalByHoscode(String hoscode);

    List<Hospital> findHospitalByHosnameLike(String hosname);
}
