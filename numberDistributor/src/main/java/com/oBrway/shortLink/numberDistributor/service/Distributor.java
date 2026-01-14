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

    void deleteKey(String key);
}
