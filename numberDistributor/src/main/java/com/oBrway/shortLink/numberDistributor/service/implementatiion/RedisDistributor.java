package com.oBrway.shortLink.numberDistributor.service.implementatiion;

import com.oBrway.shortLink.common.enums.ServiceDistributorKey;
import com.oBrway.shortLink.common.exception.BaseException;
import com.oBrway.shortLink.common.enums.ResponseCode;
import com.oBrway.shortLink.numberDistributor.service.Distributor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.concurrent.LinkedBlockingDeque;

@Service
@Slf4j
public class RedisDistributor implements Distributor {
    private Jedis jedis;

    @Value("${spring.redis.host}")
    private String REDIS_HOST;

    @Value("${spring.redis.port}")
    private int REDIS_PORT;

    public RedisDistributor() {
    }

    /**
     * 不能在构造函数中初始化Jedis连接，因为此时@Value注解的值还未注入
     */
    private void init() {
        this.jedis = new Jedis(REDIS_HOST, REDIS_PORT);
    }

    @Override
    public Long getNumberFromDistributor(ServiceDistributorKey key) throws Exception {
        try{
            init();// 初始化Jedis连接
            String serviceKey = key.getKey();
            int expireTime = key.getExpireTime();
            // Redis原子操作，序列号从1开始
            long sequence = jedis.incr(serviceKey) + key.getStartIndex();
            jedis.expire(serviceKey, expireTime);
            return sequence;
        } catch (Exception e) {
            log.error("Error getting number from RedisDistributor", e);
            throw new BaseException(e.getMessage(), ResponseCode.NUMBER_DISTRIBUTOR_ERROR);
        }

    }

    @Override
    public LinkedBlockingDeque<Long> getBatchNumberFromDistributor(ServiceDistributorKey key, int batchSize) throws Exception {
        try{
            init();
            String serviceKey = key.getKey();
            int expireTime = key.getExpireTime();
            LinkedBlockingDeque<Long> batchNumbers = new LinkedBlockingDeque<>();
            for (int i = 0; i < batchSize; i++) {
                long sequence = jedis.incr(serviceKey) + key.getStartIndex();
                batchNumbers.add(sequence);
            }
            jedis.expire(serviceKey, expireTime);
            return batchNumbers;
        } catch (Exception e) {
            log.error("Error getting batch numbers from RedisDistributor", e);
            throw new BaseException(e.getMessage(), ResponseCode.NUMBER_DISTRIBUTOR_ERROR);
        }
    }

    public void deleteKey(String key) {
        jedis.del(key);
    }
}
