package com.hloong.xiexing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
@EnableScheduling
@EnableConfigurationProperties
@MapperScan("com.hloong.xiexing.mapper")
public class XieXingApplication {

    public static void main(String[] args) {
        SpringApplication.run(XieXingApplication.class, args);
    }

}
