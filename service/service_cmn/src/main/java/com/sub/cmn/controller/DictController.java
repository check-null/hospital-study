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

    @ApiOperation("节点树")
    @GetMapping("/find-child/{id}")
    public Result<List<Dict>> findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChild(id);
        return Result.ok(list);
    }

    @GetMapping("/export")
    public void exportDict(HttpServletResponse response) {
        dictService.exportDict(response);
    }

    @PostMapping("/import")
    public Result<Boolean> importDict(MultipartFile file) {
        dictService.importDict(file);
        return Result.ok();
    }
}
