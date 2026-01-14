package com.oBrway.shortLink.client.distributorTest;

import com.oBrway.shortLink.common.enums.ServiceDistributorKey;
import com.oBrway.shortLink.numberDistributor.service.Distributor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = com.oBrway.shortLink.numberDistributor.NumberDistributorApplication.class)
public class RedisDistributorTest {
    @Autowired
    @Qualifier("redisDistributor")
    Distributor distributor;

    @Test
    public void testGetNumberFromDistributor() throws Exception {
        ServiceDistributorKey key = ServiceDistributorKey.testService;
        Long number1 = distributor.getNumberFromDistributor(key);
        Long number2 = distributor.getNumberFromDistributor(key);
        assertEquals(key.getStartIndex() + 1L, number1);
        assertEquals(key.getStartIndex() + 2L, number2);
        distributor.deleteKey(key.getKey());
    }

    @Test
    public void testGetBatchNumberFromDistributor() throws Exception {
        ServiceDistributorKey key = ServiceDistributorKey.testService;
        int batchSize = 5;
        LinkedBlockingDeque<Long> batchNumbers = distributor.getBatchNumberFromDistributor(key, batchSize);
        for (int i = 0; i < batchSize; i++) {
            assertEquals(key.getStartIndex() + 1L + i, batchNumbers.poll());
        }
        distributor.deleteKey(key.getKey());
    }
}
