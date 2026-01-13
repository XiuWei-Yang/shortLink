package com.oBrway.shortLink.core.config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.oBrway.shortLink.core.respository.sql")
public class MyBatisConfig {
}