package com.oBrway.shortLink.core.respository.cache;

import com.oBrway.shortLink.core.model.ShortLinkMappingInfo;

public interface CacheLayer {
    void fresh();
    void put(long key, ShortLinkMappingInfo value);

    ShortLinkMappingInfo get(long key);

    void delete(long key);
}
