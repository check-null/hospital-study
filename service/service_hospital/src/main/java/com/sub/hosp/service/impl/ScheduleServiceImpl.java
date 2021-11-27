package com.sub.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sub.hosp.repository.ScheduleRepository;
import com.sub.hosp.service.DepartmentService;
import com.sub.hosp.service.HospitalService;
import com.sub.hosp.service.ScheduleService;
import com.sub.model.hosp.Schedule;
import com.sub.vo.hosp.BookingScheduleRuleVo;
import com.sub.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Europa
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    ScheduleRepository scheduleRepository;

    @Resource
    MongoTemplate mongoTemplate;

    @Resource
    HospitalService hospitalService;

    @Resource
    DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> paramMap) {
        String s = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(s, Schedule.class);

        // 医院编号 和 排班编号
        Schedule exist = scheduleRepository.getByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (exist != null) {
            exist.setUpdateTime(new Date());
            exist.setIsDeleted(0);
            exist.setStatus(1);
            scheduleRepository.save(exist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo queryVo) {

        Pageable of = PageRequest.of(page - 1, limit);
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(queryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, matcher);

        return scheduleRepository.findAll(example, of);
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getRuleSchedule(Integer page, Integer limit, String hoscode, String depcode) {
        // MongoDB的聚合操作有空去学习一下, 此段代码在p95
        Criteria criteria = Criteria.where("hoscode")
                .is(hoscode)
                .and("depcode")
                .is(depcode);

        // 条件匹配
        MatchOperation match = Aggregation.match(criteria);
        // 分组
        GroupOperation groupOperation = Aggregation.group("workDate")
                .first("workDate")
                .as("workDate")
                .count()
                .as("docCount")
                .sum("reservedNumber")
                .as("reservedNumber")
                .sum("availableNumber")
                .as("availableNumber");
        // 排序
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "workDate");
        // 翻页 虽然这个方法过期了,但实际上只是把int换成了long而已
        SkipOperation skip = Aggregation.skip((page - 1) * limit);
        // size
        LimitOperation size = Aggregation.limit(limit);
        // 根据工作日期workDate分组
        Aggregation agg = Aggregation.newAggregation(match, groupOperation, sort, skip, size);
        // 组装完成后,最终交给MongoDB处理
        AggregationResults<BookingScheduleRuleVo> rules = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> list = rules.getMappedResults();
        // 分组查询的记录总数
        Aggregation totalAgg = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.group("workDate"));
        AggregationResults<BookingScheduleRuleVo> totalAggResult = mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResult.getMappedResults().size();
        // 把对应的日期换成星期
        list.forEach(vo -> {
            Date workDate = vo.getWorkDate();
            String dayOfWeek = getDayOfWeek(new DateTime(workDate));
            vo.setDayOfWeek(dayOfWeek);
        });

        // 获得医院名称
        String hospName = hospitalService.getHospName(hoscode);
        HashMap<String, String> hashMap = new HashMap<>(16);
        hashMap.put("hosname", hospName);

        // 最终返回组装完成的数据
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("bookingScheduleRuleList", list);
        map.put("total", total);
        map.put("baseMap", hashMap);
        return map;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        if (StringUtils.isEmpty(workDate) || "null".equals(workDate)) {
            return null;
        }
        /*
            预期组装条件 { "hoscode" : "1000_0", "depcode" : "200040878", "workDate" : { "$date" : 1611676800000}}
            实际组装条件 {"find": "Schedule", "filter": {"hoscode": "1000_0", "depcode": "200040878", "workDate": {"$date": "2021-01-26T16:00:00Z"}}, "$db": "yygh_hosp"}
            原本的日期会莫名其妙的-1, 导致查到前一天的数据
         */
        List<Schedule> list = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        list.forEach(this::packageSchedule);
        return list;
    }

    private void packageSchedule(Schedule schedule) {
        // 这2个都是MongoDB里面查询出来的结果,所以比mysql快很多
        String hospName = hospitalService.getHospName(schedule.getHoscode());
        schedule.getParam().put("hosname", hospName);

        String depName = departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode());
        schedule.getParam().put("depname", depName);

        String dayOfWeek = getDayOfWeek(new DateTime(schedule.getWorkDate()));
        schedule.getParam().put("dayOfWeek", dayOfWeek);
    }

    /**
     * 根据日期获取周几数据
     *
     * @param dateTime DateTIme
     * @return 星期
     */
    private String getDayOfWeek(DateTime dateTime) {
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                return "周日";
            case DateTimeConstants.MONDAY:
                return "周一";
            case DateTimeConstants.TUESDAY:
                return "周二";
            case DateTimeConstants.WEDNESDAY:
                return "周三";
            case DateTimeConstants.THURSDAY:
                return "周四";
            case DateTimeConstants.FRIDAY:
                return "周五";
            case DateTimeConstants.SATURDAY:
                return "周六";
            default:
                return "";
        }
    }

}
