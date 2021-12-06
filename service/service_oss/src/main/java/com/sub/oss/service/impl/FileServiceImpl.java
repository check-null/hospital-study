package com.sub.oss.service.impl;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.sub.oss.component.OssComponent;
import com.sub.oss.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Resource
    OssComponent ossComponent;

    @Override
    public String upload(MultipartFile file) {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = ossComponent.getEndpoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ossComponent.getAccessKeyId();
        String accessKeySecret = ossComponent.getAccessKeySecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ossComponent.getBucketName();

        String filePath = null;
        OSS ossClient = null;
        try (InputStream inputStream = file.getInputStream()) {
            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 填写文件名。文件名包含路径，不包含Bucket名称。例如exampledir/exampleobject.txt。
            String filename = file.getOriginalFilename();

            // 获得文件格式
            assert filename != null;
            int i = filename.lastIndexOf(".");
            String suffix = filename.substring(i);
            String name = UUID.randomUUID().toString() + suffix;
            // 设置文件的目录(按照年月日)与名字
            String path = DateUtil.format(new Date(), "yyyy/MM/dd")+ "/" + name;
            // 上传文件
            ossClient.putObject(bucketName, path, inputStream);
            filePath = "https://" + bucketName + "." + endpoint + "/" + path;
            System.out.println(filePath);
        } catch (OSSException | IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭OSSClient。
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return filePath;
    }
}
