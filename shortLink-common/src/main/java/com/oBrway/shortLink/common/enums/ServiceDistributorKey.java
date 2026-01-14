package com.oBrway.shortLink.common.enums;

import lombok.Getter;

@Getter
public enum ServiceDistributorKey {
    coreService("coreServiceKey", 56800235584L, 20*365*24*60*60), // 20 years
    testService("testServiceKey", 100L, 60)
    ;

    private String key;
    private long startIndex;
    private int expireTime;

    ServiceDistributorKey(String coreServiceKey, long startIndex, int expireTime) {
        this.key = coreServiceKey;
        this.startIndex = startIndex;
        this.expireTime = expireTime;
    }
}
