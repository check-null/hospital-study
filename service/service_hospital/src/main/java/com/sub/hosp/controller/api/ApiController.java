package com.sub.hosp.controller.api;

import com.sub.common.exception.YyghException;
import com.sub.common.helper.HttpRequestHelper;
import com.sub.common.result.Result;
import com.sub.common.result.ResultCodeEnum;
import com.sub.common.utils.MD5;
import com.sub.hosp.service.DepartmentService;
import com.sub.hosp.service.HospitalService;
import com.sub.hosp.service.HospitalSetService;
import com.sub.model.hosp.Hospital;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Europa
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Resource
    HospitalService hospitalService;

    @Resource
    HospitalSetService hospitalSetService;

    @Resource
    DepartmentService departmentService;

    @PostMapping("/saveDepartment")
    public Result<Boolean> saveDepartment(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        String sign = (String) paramMap.get("sign");
        String hoscode = (String) paramMap.get("hoscode");

        String key = hospitalSetService.getSignKey(hoscode);

        String signKeyMD5 = MD5.encrypt(key);

        if (!sign.equals(signKeyMD5)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.save(paramMap);

        return Result.ok();
    }


    @PostMapping("/hospital/show")

    public Result<Hospital> getHospital(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        String sign = (String) paramMap.get("sign");
        String hoscode = (String) paramMap.get("hoscode");

        String key = hospitalSetService.getSignKey(hoscode);

        String signKeyMD5 = MD5.encrypt(key);

        if (!sign.equals(signKeyMD5)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    /**
     * 上传医院
     *
     * @param request
     * @return
     */
    @PostMapping("/saveHospital")
    public Result<Boolean> save(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        String sign = (String) paramMap.get("sign");
        String hoscode = (String) paramMap.get("hoscode");

        String key = hospitalSetService.getSignKey(hoscode);

        String signKeyMD5 = MD5.encrypt(key);

        if (!sign.equals(signKeyMD5)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logData", logoData);
        hospitalService.save(paramMap);
        return Result.ok();
    }


}
