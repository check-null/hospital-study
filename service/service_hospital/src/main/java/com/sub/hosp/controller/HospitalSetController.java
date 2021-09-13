package com.sub.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sub.common.result.Result;
import com.sub.common.utils.MD5;
import com.sub.hosp.service.HospitalSetService;
import com.sub.model.hosp.HospitalSet;
import com.sub.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Resource
    private HospitalSetService hospitalSetService;

    @ApiOperation("获取所有医院设置")
    @GetMapping("/findAll")
    public Result<List<HospitalSet>> list() {
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @ApiOperation("逻辑删除医院设置")
    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean b = hospitalSetService.removeById(id);
        return b ? Result.ok() : Result.fail();
    }

    @ApiOperation("分页")
    @PostMapping("/page/{current}/{limit}")
    public Result<IPage<HospitalSet>> page(@PathVariable long current,
                                           @PathVariable long limit,
                                           @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current, limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        //医院名称
        if (hospitalSetQueryVo != null) {
            String hosname = hospitalSetQueryVo.getHosname();
            //医院编号
            String hoscode = hospitalSetQueryVo.getHoscode();
            if (!StringUtils.isEmpty(hosname)) {
                wrapper.like("hosname", hospitalSetQueryVo.getHosname());
            }
            if (!StringUtils.isEmpty(hoscode)) {
                wrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
            }
        }
        //调用方法实现分页查询
        IPage<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);

        //返回结果
        return Result.ok(pageHospitalSet);
    }

    @ApiOperation("添加")
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody HospitalSet hospitalSet) {
        // 状态 0:no 1:ok
        hospitalSet.setStatus(1);
        // md5签名加密
        Random random = new Random();
        String encrypt = MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000));
        hospitalSet.setSignKey(encrypt);

        boolean b = hospitalSetService.save(hospitalSet);
        return b ? Result.ok() : Result.fail();
    }

    @ApiOperation("获取信息")
    @GetMapping("/info/{id}")
    public Result<HospitalSet> info(@PathVariable Long id) {
        HospitalSet data = hospitalSetService.getById(id);
        return Result.ok(data);
    }

    @ApiOperation("修改医院信息")
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody HospitalSet hospitalSet) {
        boolean b = hospitalSetService.updateById(hospitalSet);
        return b ? Result.ok() : Result.fail();
    }

    @ApiOperation("批量删除")
    @PostMapping("/batch-delete")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        boolean b = hospitalSetService.removeByIds(ids);
        return b ? Result.ok() : Result.fail();
    }

    @PostMapping("/lock/{id}/{status}")
    public Result<Boolean> lock(@PathVariable Long id, @PathVariable Integer status) {
        HospitalSet data = hospitalSetService.getById(id);
        data.setStatus(status);

        boolean b = hospitalSetService.updateById(data);
        return b ? Result.ok() : Result.fail();
    }

    @PostMapping("/send-key/{id}")
    public Result<Boolean> send(@PathVariable Long id) {
        HospitalSet data = hospitalSetService.getById(id);

        String signKey = data.getSignKey();
        String hoscode = data.getHoscode();
        // todo 发短信
        return Result.ok();
    }
}
