package com.oBrway.shortLink.core.config;

import lombok.Data;

@Data
public class Config {
    private String serviceId = "short-link-service";

    private String registryAddress = "localhost:8888";

    private String env = "dev";

    private static final long shortLink_length = 7;
    //短链接有效时间
    private static final long shortLink_validTime = 60 * 60 * 24 * 30; // 30天
    //发号器每次调用获取到的号数量
    private static final int idGenerator_step = 100;
}
