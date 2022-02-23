package com.sub.user.client;

import com.sub.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-user")
public interface PatientFeignClient {

    @GetMapping("api/user/patient/inner/get/{id}")
    Patient getPatientOrder(@PathVariable("id") Long id);

    @GetMapping("api/user/patient/auth/get/{id}")
    Patient getPatient(@PathVariable Long id);
}
