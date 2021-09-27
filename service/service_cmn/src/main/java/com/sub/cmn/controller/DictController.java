package com.sub.cmn.controller;

import com.sub.cmn.service.DictService;
import com.sub.common.result.Result;
import com.sub.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Resource
    DictService dictService;

    @GetMapping("/findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(@PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value) {

        return dictService.getDictName(dictCode, value);
    }

    @GetMapping("/getName/{value}")
    public String getName(@PathVariable String value) {

        return dictService.getDictName("", value);
    }

    @ApiOperation("节点树")
    @GetMapping("/findChildData/{id}")
    public Result<List<Dict>> findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChild(id);
        return Result.ok(list);
    }

    @ApiOperation("导出excel")
    @GetMapping("/export")
    public void exportDict(HttpServletResponse response) {
        dictService.exportDict(response);
    }

    @ApiOperation("导入excel")
    @PostMapping("/import")
    public Result<Boolean> importDict(MultipartFile file) {
        dictService.importDict(file);
        return Result.ok();
    }
}
