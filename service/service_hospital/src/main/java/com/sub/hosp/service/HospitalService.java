package com.sub.hosp.service;

import com.sub.model.hosp.Hospital;
import com.sub.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @author Europa
 */
public interface HospitalService {

    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo queryVo);

    void updateStatus(String id, Integer status);

    Map<String, Object> getHospById(String id);
}
