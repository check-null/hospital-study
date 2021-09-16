package com.sub.hosp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sub.hosp.repository.HospitalRepository;
import com.sub.hosp.service.HospitalService;
import com.sub.model.hosp.Hospital;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Resource
    private HospitalRepository hospitalRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        String str = JSON.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(str, Hospital.class);

        String hoscode = hospital.getHoscode();
        Hospital hosp = hospitalRepository.getHospitalByHoscode(hoscode);

        // 不存在则添加 反之修改
        if (hosp != null) {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
        } else {
            hospital.setStatus(hospital.getStatus());
            hospital.setCreateTime(hospital.getCreateTime());
        }
        hospital.setIsDeleted(0);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }
}
