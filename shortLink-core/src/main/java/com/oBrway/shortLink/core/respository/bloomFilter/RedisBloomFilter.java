package com.oBrway.shortLink.core.respository.bloomFilter;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class RedisBloomFilter implements BloomFilter {

    private final RBloomFilter<String> bloomFilter;

    public RedisBloomFilter(RedissonClient redissonClient) {
        this.bloomFilter = redissonClient.getBloomFilter("shortLinkBloomFilter");
        bloomFilter.tryInit(1000000L, 0.01); // 初始化：预计插入 100 万条数据，误判率 1%
    }

    @Override
    public void add(String value) {
        bloomFilter.add(value);
    }

    @Override
    public boolean contains(String value) {
        return bloomFilter.contains(value);
    }
}