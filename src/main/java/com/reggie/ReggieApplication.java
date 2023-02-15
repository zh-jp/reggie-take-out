package com.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j //输出日志
@EnableTransactionManagement // 开启事务注解支持
@ServletComponentScan //扫描WebFilter注解
@EnableCaching  // 开启Spring Cache 注解方式的缓存功能
@SpringBootApplication
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ReggieApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
        log.info("===========Project Start===========");
    }
}
