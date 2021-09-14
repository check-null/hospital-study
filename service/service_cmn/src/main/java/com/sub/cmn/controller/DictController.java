package com.sub.cmn.controller;

import com.sub.cmn.service.DictService;
import com.sub.common.result.Result;
import com.sub.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "数据字典")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Resource
    DictService dictService;

    @ApiOperation("节点树")
    @GetMapping("/find-child/{id}")
    public Result<List<Dict>> findChildData(@PathVariable Long id) {
       List<Dict> list = dictService.findChild(id);
        return Result.ok(list);
    }
}
