package com.oBrway.shortLink.numberDistributor.service.Impl;

import com.oBrway.shortLink.common.enums.ServiceDistributorKey;
import com.oBrway.shortLink.common.exception.BaseException;
import com.oBrway.shortLink.common.enums.ResponseCode;
import com.oBrway.shortLink.numberDistributor.service.Distributor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;

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

    /**
     * 生产环境中不要使用
     */
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

    /**
     * 返回号段，LinkedList中是{起始号码，结束号码}
     * @param key
     * @param batchSize
     * @return
     * @throws Exception
     */
    @Override
    public List<Long> getBatchNumberFromDistributor(ServiceDistributorKey key, int batchSize) throws Exception {
        try{
            init();
            String serviceKey = key.getKey();
            int expireTime = key.getExpireTime();
            List<Long> batchNumbers = new LinkedList<>();
            long end = jedis.incrBy(serviceKey, batchSize);
            long start = end - batchSize + 1;//左边界不取
            batchNumbers.add(start);
            batchNumbers.add(end);
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
