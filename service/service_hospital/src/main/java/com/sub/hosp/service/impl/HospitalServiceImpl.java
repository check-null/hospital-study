package com.sub.hosp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sub.cmn.client.DictFeignClient;
import com.sub.hosp.repository.HospitalRepository;
import com.sub.hosp.service.HospitalService;
import com.sub.model.hosp.Department;
import com.sub.model.hosp.Hospital;
import com.sub.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Resource
    private HospitalRepository hospitalRepository;

    @Resource
    private DictFeignClient dictFeignClient;

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

    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo queryVo) {
        Pageable of = PageRequest.of(page - 1, limit);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(queryVo, hospital);
        hospital.setIsDeleted(0);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Hospital> example = Example.of(hospital, matcher);

        Page<Hospital> all = hospitalRepository.findAll(example, of);
        all.getContent().forEach(this::setHospitalHosType);
        return all;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String, Object> map = new HashMap<>(16);
        Hospital hosp = hospitalRepository.findById(id).get();

        setHospitalHosType(hosp);
        // 医院基本信息
        map.put("hospital", hosp);
        // 预约规则,从里面提出来
        map.put("bookingRule", hosp.getBookingRule());
        // 已经提出来了,不需要重复显示
        hosp.setBookingRule(null);
        return map;
    }

    private void setHospitalHosType(Hospital hosp) {
        String hostype = dictFeignClient.getName("Hostype", hosp.getHostype());
        String province = dictFeignClient.getName(hosp.getProvinceCode());
        String city = dictFeignClient.getName(hosp.getCityCode());
        String district = dictFeignClient.getName(hosp.getDistrictCode());

        hosp.getParam().put("fullAddress", province + city + district);
        hosp.getParam().put("hostypeString", hostype);
    }

}
