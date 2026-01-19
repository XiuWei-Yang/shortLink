package com.oBrway.shortLink.core.respository.cache;

import com.oBrway.shortLink.core.model.ShortLinkMappingInfo;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheLayer implements CacheLayer {
    private static final long TIMEOUT = 10;

    private final RedisTemplate<String, ShortLinkMappingInfo> redisTemplate;

    public RedisCacheLayer() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory("localhost", 6379);
        connectionFactory.afterPropertiesSet(); // 初始化连接工厂
        this.redisTemplate = new RedisTemplate<>();
        this.redisTemplate.setConnectionFactory(connectionFactory);
        // 设置序列化器
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet(); // 初始化模板
    }

    @Override
    public void fresh() {
        // Redis 不需要手动清理缓存，使用过期时间即可
    }

    @Override
    public void put(long key, ShortLinkMappingInfo value) {
        ValueOperations<String, ShortLinkMappingInfo> ops = redisTemplate.opsForValue();
        ops.set(String.valueOf(key), value, TIMEOUT, TimeUnit.MINUTES); // 设置过期时间为 10 分钟
    }

    @Override
    public ShortLinkMappingInfo get(long key) {
        ValueOperations<String, ShortLinkMappingInfo> ops = redisTemplate.opsForValue();
        return ops.get(String.valueOf(key));
    }

    @Override
    public void delete(long key) {
        redisTemplate.delete(String.valueOf(key));
    }

    public void updateExpireTime(long key) {
        redisTemplate.expire(String.valueOf(key), TIMEOUT, TimeUnit.MINUTES);
    }
}