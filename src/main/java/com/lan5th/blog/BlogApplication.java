package com.lan5th.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@MapperScan("com.lan5th.blog.dao")
@SpringBootApplication
@EnableScheduling
public class BlogApplication {
    //设置无法配置的系统参数
    static {
        //druid超过60s连接池报错
        System.setProperty("druid.mysql.usePingMethod","false");
    }

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
