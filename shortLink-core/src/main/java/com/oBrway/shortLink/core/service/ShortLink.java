package com.oBrway.shortLink.core.service;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

import com.oBrway.shortLink.common.enums.ResponseCode;
import com.oBrway.shortLink.common.exception.BaseException;
import com.oBrway.shortLink.core.respository.ShortLinkDAO;
import com.oBrway.shortLink.core.service.Base62.Base62Encoder;
import com.oBrway.shortLink.core.service.IDGenerator.IDGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@Setter
@Scope("prototype")
/**
 * 不要一直用一样的长链接生成短链接，否则一个长链接对应多个短链接，会造成资源的浪费
 */
public class ShortLink {
    private LinkedBlockingDeque<Long> idBuffer = new LinkedBlockingDeque<>();

    @Autowired
    @Qualifier("IDGeneratorInvoker")
    private IDGenerator DistributedIdGenerator;

    @Getter
    @Autowired
    private ShortLinkDAO shortLinkDAO;

    private ReentrantLock lock = new ReentrantLock();

    public ShortLink() {
    }

    /**
     * 生成短链接
     * 使用分布式取号器获取唯一ID，转换为短链接字符串
     * 分布式取号器一次给出多个ID，缓存在本地内存中，当内存中无可用ID时，再次从分布式取号器获取一批ID
     * 布隆过滤器
     * @param originalUrl
     * @return
     */
    public String generateAndStoreShortLink(String originalUrl) throws Exception {
        if(originalUrl == null || originalUrl.isEmpty()) {
            throw new BaseException("Original URL is empty", ResponseCode.SHORT_LINK_GENERATE_ERROR);
        }
        try{
            long id = getIdFormBuffer();
            String shortLink = Base62Encoder.encode(id);
            shortLinkDAO.storeShortLinkMapping(id, shortLink, originalUrl);
            return shortLink;
        } catch (Exception e) {
            log.error("Error generating short link for URL {}: {}", originalUrl, e.getMessage());
            throw new BaseException(e.getMessage(), ResponseCode.SHORT_LINK_GENERATE_ERROR);
        }
    }

    /**
     * 如果取号数量和同时进入的请求数的乘积不大，可以考虑不加锁，此时一定的浪费比锁带来的不确定性更能接收（不加锁的方案很简单，在这里不提供）
     * 还有一种实现，需要两把锁，一把锁保护取号，另一把锁用于提示等待取号完成，但是取号需要获取两把锁，在生产者消费者的关系不是非常清晰的情况下，可能会导致不可知情况的出现，严重影响性能
     * 如果id队列为空，则检查取号锁，如果没有其他线程在取号，则进行取号，完成后唤醒所有线程；如果有线程取号，则等待唤醒
     */
    private long getIdFormBuffer() throws Exception {
        if(idBuffer.isEmpty()) {
            if(lock.tryLock()) {
                try{
                    getIdsToBuffer();
                } catch (Exception e) {
                    throw new BaseException(e.getMessage(), ResponseCode.ID_GENERATOR_INVOKE_ERROR);
                } finally {
                    lock.unlock();
                }
            } else{
                // 等3秒，超时后继续执行remove，如果取号器超时则队列空，会抛出异常
                sleep(3000);
            }
        }
        try{
            return idBuffer.remove();
        } catch (Exception e) {
            log.error("Error getting ID from buffer: {}", e.getMessage());
            //这里队列空是由于其他线程的取号器超时导致的，因此抛出超时异常，可以更快定位问题
            throw new BaseException("No IDs available", ResponseCode.ID_GENERATOR_INVOKE_TIMEOUT);
        }
    }

    /**
     * springMVC是多线程模型
     * 两个线程同时取号时，可能会导致多次取号，产生ID浪费
     * 一个线程取号时，其他线程等待，直到取号完成，但不重新取号
     */
    private void getIdsToBuffer() throws Exception {
        idBuffer = DistributedIdGenerator.getNextID();
    }

    /**
     * 根据短链接获取原始URL
     * 查询缓存或数据库获取原始URL，将查询结果加入缓存，并且进行日志打印
     * @param shortLink
     * @return
     */
    public String getOriginalUrl(String shortLink) throws Exception {
        if(shortLink == null || shortLink.isEmpty()) {
            log.error("Short link is empty");
            throw new BaseException("Short link is empty", ResponseCode.SHORT_LINK_QUERY_ERROR);
        }
        try {
            long id = Base62Encoder.decodeToLong(shortLink);
            return shortLinkDAO.queryShortLink(id, shortLink);
        } catch (Exception e) {
            log.error("Error querying short link {}: {}", shortLink, e.getMessage());
            throw new BaseException(e.getMessage(), ResponseCode.SHORT_LINK_QUERY_ERROR);
        }
    }
}
