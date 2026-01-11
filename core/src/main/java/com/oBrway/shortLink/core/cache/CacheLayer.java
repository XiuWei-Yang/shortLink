package com.oBrway.shortLink.core.cache;

public interface CacheLayer {
    void init();
    void put(String key, String value);

    String get(String key);

    void delete(String key);
}
