package com.sub.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sub.common.exception.YyghException;
import com.sub.common.result.ResultCodeEnum;
import com.sub.hosp.repository.ScheduleRepository;
import com.sub.hosp.service.DepartmentService;
import com.sub.hosp.service.HospitalService;
import com.sub.hosp.service.ScheduleService;
import com.sub.model.hosp.BookingRule;
import com.sub.model.hosp.Department;
import com.sub.model.hosp.Hospital;
import com.sub.model.hosp.Schedule;
import com.sub.vo.hosp.BookingScheduleRuleVo;
import com.sub.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {
        HashMap<String, Object> result = new HashMap<>(16);
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //获得可预约日期数据
        IPage<Date> iPage = this.getListDate(page, limit, bookingRule);
        List<Date> dateList = iPage.getRecords();
        //获得可预约日期里面科室的剩余预约数
        Criteria criteria = Criteria.where("hoscode")
                .is(hoscode)
                .and("depcode")
                .is(depcode)
                .and("workDate")
                .in(dateList);

        MatchOperation match = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("workDate")
                .first("workDate")
                .as("workDate")
                .count()
                .as("docCount")
                .sum("availableNumber")
                .as("availableNumber")
                .sum("reservedNumber")
                .as("reservedNumber");
        Aggregation agg = Aggregation.newAggregation(match, groupOperation);

        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregationResults.getMappedResults();

        //合并数据 map集合 key日期 value预约规则和剩余数量等
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream()
                    .collect(
                            Collectors.toMap(
                                    BookingScheduleRuleVo::getWorkDate,
                                    bookingScheduleRuleVo -> bookingScheduleRuleVo
                            )
                    );
        }
        //获取可预约排班规则
        ArrayList<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();

        int len = dateList.size();
        for (int i = 0; i < len; i++) {
            Date date = dateList.get(i);

            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            if (null == bookingScheduleRuleVo) { // 说明当天没有排班医生
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if (i == len - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            if (i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department = departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;

    }

    private IPage<Date> getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        //获得当天放号时间 年月日时分
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //获得预约周期
        Integer cycle = bookingRule.getCycle();
        //如果当天放号时间已经过去了,预约周期从后一天开始计算,周期+1
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }
        //预约可预约所有日期,最后一天显示即将放号
        ArrayList<Date> dateList = new ArrayList<>(cycle);
        for (int i = 0; i < cycle; i++) {
            DateTime currentTime = new DateTime().plusDays(1);
            String dateString = currentTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }
        //因为预约周期不同,每页显示日期最多7天数据,超过7天的分页
        ArrayList<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = (page - 1) * limit + limit;
        if (end > dateList.size()) {
            end = dateList.size();
        }
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, 7, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    private DateTime getDateTime(Date date, String time) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + time;
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
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
