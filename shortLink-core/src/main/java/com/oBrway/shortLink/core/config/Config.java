package com.oBrway.shortLink.core.config;

import lombok.Data;

@Data
public class Config {
    private int port = 8858;

    private String serviceId = "short-link-service";

    private String registryAddress = "localhost:8888";

    private String env = "dev";

    private long shortLink_length = 7;
    //短链接有效时间
    private long shortLink_validTime = 60 * 60 * 24 * 30; // 30天
    //发号器每次调用获取到的号数量
    private int idGenerator_step = 100;
    //布隆过滤器预估数据量
    private int bloomFilter_expectedInsertions = 1000000;
}
