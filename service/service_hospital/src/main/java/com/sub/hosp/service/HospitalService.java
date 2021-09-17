package com.sub.hosp.service;

import com.sub.model.hosp.Hospital;

import java.util.Map;

/**
 * @author Europa
 */
public interface HospitalService {

    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);
}
