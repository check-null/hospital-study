package com.sub.oss.controller;

import com.sub.common.result.Result;
import com.sub.oss.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/oss/file")
public class FileApiController {

    @Resource
    FileService fileService;

    @PostMapping("fileUpload")
    public Result<String> fileUpload(MultipartFile file) {
        String path = fileService.upload(file);
        return Result.ok(path);
    }
}
