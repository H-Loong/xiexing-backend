package com.hloong.xiexing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class COSConfig {
    // 存储桶名称
    private String bucketName;
    // secretId 秘钥id
    private String secretId;
    // secretKey 秘钥
    private String secretKey;
    // 腾讯云 自定义文件夹名称
    private String prefix;
    // 访问域名
    private String url;
    // 地区
    private String region;
}
