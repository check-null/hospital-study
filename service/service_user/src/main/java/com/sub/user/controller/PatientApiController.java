package com.sub.user.controller;

import com.sub.common.result.Result;
import com.sub.common.utils.AuthContextHolder;
import com.sub.model.user.Patient;
import com.sub.user.service.PatientService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/user/patient")
public class PatientApiController {

    @Resource
    PatientService patientService;

    @GetMapping("auth/findAll")
    public Result<Object> findAll(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.findAllUserId(userId);
        return Result.ok(list);
    }

    @PostMapping("auth/save")
    public Result<Boolean> save(@RequestBody Patient patient, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        // todo bug 传过来后生日时间会-1天
        boolean save = patientService.save(patient);
        return Result.ok(save);
    }

    @GetMapping("auth/get/{id}")
    public Result<Object> getPatient(@PathVariable Long id) {
        Patient patient = patientService.getByPatientId(id);
        return Result.ok(patient);
    }

    @GetMapping("auth/remove/{id}")
    public Result<Boolean> removeById(@PathVariable Long id) {
        // todo bug 传过来后生日时间会-1天
        boolean b = patientService.removeById(id);
        return Result.ok(b);
    }

    @PostMapping("auth/update")
    public Result<Object> updatePatient(@RequestBody Patient patient) {
        boolean b = patientService.updateById(patient);
        return Result.ok(b);
    }


}
