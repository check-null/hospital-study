package com.sub.hosp.controller.api;

import com.sub.common.helper.HttpRequestHelper;
import com.sub.common.result.Result;
import com.sub.hosp.service.HospitalService;
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

    @PostMapping("/save")
    public Result save(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        hospitalService.save(paramMap);
        return null;
    }
}
