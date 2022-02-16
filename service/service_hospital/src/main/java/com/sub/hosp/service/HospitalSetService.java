package com.sub.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sub.model.hosp.HospitalSet;
import com.sub.vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {

    String getSignKey(String hoscode);

    SignInfoVo getSignInfoVo(String hoscode);
}
