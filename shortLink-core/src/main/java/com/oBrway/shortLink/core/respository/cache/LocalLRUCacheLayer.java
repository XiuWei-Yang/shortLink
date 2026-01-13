package com.oBrway.shortLink.core.respository.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.oBrway.shortLink.core.model.ShortLinkMappingInfo;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class LocalLRUCacheLayer implements CacheLayer {

    private final Cache<Long, ShortLinkMappingInfo> shortLinkCache = Caffeine.newBuilder().recordStats().expireAfterWrite(60, TimeUnit.MINUTES).build();

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
}
