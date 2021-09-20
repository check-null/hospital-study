package com.sub.hosp.controller.api;

import com.sub.common.exception.YyghException;
import com.sub.common.helper.HttpRequestHelper;
import com.sub.common.result.Result;
import com.sub.common.result.ResultCodeEnum;
import com.sub.common.utils.MD5;
import com.sub.hosp.service.DepartmentService;
import com.sub.hosp.service.HospitalService;
import com.sub.hosp.service.HospitalSetService;
import com.sub.hosp.service.ScheduleService;
import com.sub.model.hosp.Department;
import com.sub.model.hosp.Hospital;
import com.sub.model.hosp.Schedule;
import com.sub.vo.hosp.DepartmentQueryVo;
import com.sub.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
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

    @Resource
    ScheduleService scheduleService;

    @PostMapping("/schedule/remove")
    public Result<Boolean> removeSchedule(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        String hoscode = (String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");


        scheduleService.remove(hoscode, hosScheduleId);

        return Result.ok();
    }

    @PostMapping("/schedule/list")
    public Result<Page<Schedule>> findSchedule(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        String pageStr = (String) paramMap.get("page");
        String limitStr = (String) paramMap.get("limit");

        int page = StringUtils.isEmpty(pageStr) ? 1 : Integer.parseInt(pageStr);
        int limit = StringUtils.isEmpty(limitStr) ? 1 : Integer.parseInt(limitStr);
        // 医院编号
        String hoscode = (String) paramMap.get("hoscode");
        // 科室编号
        String depcode = (String) paramMap.get("depcode");

        ScheduleQueryVo queryVo = new ScheduleQueryVo();
        queryVo.setHoscode(hoscode);
        queryVo.setDepcode(depcode);
        Page<Schedule> pageMode = scheduleService.findPageDepartment(page, limit, queryVo);

        return Result.ok(pageMode);
    }

    @PostMapping("/saveSchedule")
    public Result<Boolean> saveSchedule(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        String hoscode = (String) paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");
        String key = hospitalSetService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(key);

        if (!sign.equals(signKeyMD5)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.save(paramMap);
        return Result.ok();
    }

    @PostMapping("/department/remove")
    public Result<Boolean> removeDepartment(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");

        // todo 签名

        departmentService.remove(hoscode, depcode);

        return Result.ok();
    }

    @PostMapping("/department/list")
    public Result<Page<Department>> findDepartment(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        String hoscode = (String) paramMap.get("hoscode");
        String getPage = (String) paramMap.get("page");
        String getLimit = (String) paramMap.get("limit");

        int page = StringUtils.isEmpty(hoscode) ? 1 : Integer.parseInt(getPage);
        int limit = StringUtils.isEmpty(getLimit) ? 1 : Integer.parseInt(getLimit);

        String sign = (String) paramMap.get("sign");
        String key = hospitalSetService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(key);

        if (!sign.equals(signKeyMD5)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        DepartmentQueryVo queryVo = new DepartmentQueryVo();
        queryVo.setHoscode(hoscode);
        Page<Department> pageMode = departmentService.findPageDepartment(page, limit, queryVo);

        return Result.ok(pageMode);
    }

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
