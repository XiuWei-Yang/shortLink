package com.oBrway.shortLink.numberDistributor.service;

import com.oBrway.shortLink.common.enums.ServiceDistributorKey;

import java.util.concurrent.LinkedBlockingDeque;

public interface Distributor {
    /**
     * key 代表不同的业务线
     * @param key
     * @return
     */
    Long getNumberFromDistributor(ServiceDistributorKey key) throws Exception;

    LinkedBlockingDeque<Long> getBatchNumberFromDistributor(ServiceDistributorKey key, int batchSize) throws  Exception;

    /**
     * 删除指定key,请勿在业务代码中使用，仅供测试使用
     * @param key
     */
    void deleteKey(String key);
}
