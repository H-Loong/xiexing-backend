package com.hloong.xiexing.utils;

import com.hloong.xiexing.config.COSConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Random;

@Component
public class TencentCOSUploadFileUtil {

    @Autowired
    private COSConfig cosConfig;

    /**
     * @methodName uploadfile
     * @effect: 上传文件
     */
    public String uploadFile(MultipartFile file) {
        // 创建COS 凭证
        COSCredentials credentials = new BasicCOSCredentials(cosConfig.getSecretId(), cosConfig.getSecretKey());
        // 配置 COS 区域
        ClientConfig clientConfig = new ClientConfig(new Region(cosConfig.getRegion()));
        // 创建 COS 客户端连接
        COSClient cosClient = new COSClient(credentials, clientConfig);
        String fileName = file.getOriginalFilename();
        try {
            assert fileName != null;
            String substring = fileName.substring(fileName.lastIndexOf("."));
            File localFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), substring);
            file.transferTo(localFile);
            Random random = new Random();
            fileName = cosConfig.getPrefix() + random.nextInt(10000) + System.currentTimeMillis() + substring;
            // 将文件上传至 COS
            PutObjectRequest objectRequest = new PutObjectRequest(cosConfig.getBucketName(), fileName, localFile);
            cosClient.putObject(objectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return cosConfig.getUrl() + fileName;
    }
}
