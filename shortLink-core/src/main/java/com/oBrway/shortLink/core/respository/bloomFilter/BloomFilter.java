package com.oBrway.shortLink.core.respository.bloomFilter;

public interface BloomFilter {
    void add(String value);

    boolean contains(String value);
}
