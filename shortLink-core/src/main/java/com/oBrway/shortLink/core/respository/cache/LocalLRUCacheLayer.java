package com.oBrway.shortLink.core.respository.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.oBrway.shortLink.core.model.ShortLinkMappingInfo;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class LocalLRUCacheLayer implements CacheLayer {
    //最近使用的短链接缓存过期时间(分钟)
    private static final long TIMEOUT = 10;

    private final Cache<Long, ShortLinkMappingInfo> shortLinkCache = Caffeine.newBuilder().recordStats().expireAfterWrite(TIMEOUT, TimeUnit.MINUTES).build();

    @Override
    public void fresh() {
        shortLinkCache.cleanUp();
    }

    @Override
    public void put(long key, ShortLinkMappingInfo value) {
        shortLinkCache.put(key, value);
    }

    @Override
    public ShortLinkMappingInfo get(long key) {
        return shortLinkCache.getIfPresent(key);
    }

    @Override
    public void delete(long key) {
        shortLinkCache.invalidate(key);
    }

    @Override
    public void updateExpireTime(long key) {
        // Caffeine 不支持单独更新某个键的过期时间，过期时间是基于写入时间自动管理的
    }
}
