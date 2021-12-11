package com.sub.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sub.model.user.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {
    List<Patient> findAllUserId(Long userId);

    Patient getByPatientId(Long id);
}
