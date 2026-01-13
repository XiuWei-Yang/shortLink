package com.oBrway.shortLink.core.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.database}")
    private int dataBase;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+host+":"+port)
                .setPassword(null) // Set password if Redis requires authentication
                .setDatabase(dataBase).setTimeout(30000)
                .setConnectionPoolSize(10) // Connection pool size
                .setConnectionMinimumIdleSize(2); // Minimum idle connections

        return Redisson.create(config);
    }
}