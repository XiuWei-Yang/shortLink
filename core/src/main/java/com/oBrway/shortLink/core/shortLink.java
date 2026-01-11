package com.oBrway.shortLink.core;

import java.math.BigInteger;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.oBrway.shortLink.common.enums.ResponseCode;
import com.oBrway.shortLink.common.exception.BaseException;
import com.oBrway.shortLink.core.Base62.Base62Encoder;
import com.oBrway.shortLink.core.IDGenerator.IDGenerator;
import com.oBrway.shortLink.core.IDGenerator.Implentation.IDGeneratorInvoker;
import com.oBrway.shortLink.core.config.Config;
import lombok.extern.slf4j.Slf4j;
import com.google.common.hash.BloomFilter;

@Slf4j
public class shortLink {
    // todo: 改成Redis布隆过滤器
    // todo: 做一个LRU缓存
    private final Cache<String, String> shortLinkCache = Caffeine.newBuilder().recordStats().expireAfterWrite(30, TimeUnit.MINUTES).build();

    private LinkedBlockingDeque<Long> idBuffer = new LinkedBlockingDeque<>();

    IDGenerator DistributedIdGenerator = new IDGeneratorInvoker();

    Config config = new Config();

    private BloomFilter<String> bloomFilter = BloomFilter.create((from, into) -> {
        byte[] bytes = from.getBytes();
        into.putBytes(bytes);
    }, config.getBloomFilter_expectedInsertions(), 0.01);

    public shortLink() {
    }

    /**
     * 生成短链接
     * 使用分布式取号器获取唯一ID，转换为短链接字符串
     * 分布式取号器一次给出多个ID，缓存在本地内存中，当内存中无可用ID时，再次从分布式取号器获取一批ID
     * 布隆过滤器
     * @param originalUrl
     * @return
     */
    public String generateShortLink(String originalUrl) throws Exception {
        if(originalUrl == null || originalUrl.isEmpty()) {
            throw new BaseException("Original URL is empty", ResponseCode.SHORT_LINK_GENERATE_ERROR);
        }
        try{
            if(idBuffer.isEmpty()) {
                idBuffer = DistributedIdGenerator.getNextID();
            }
            long id = idBuffer.remove();
            BigInteger bigIntegerId = BigInteger.valueOf(id);
            String shortLink = Base62Encoder.encode(bigIntegerId);
            storeShortLinkMapping(shortLink, originalUrl);
            bloomFilter.put(shortLink);
            return shortLink;
        } catch (Exception e) {
            log.error("Error generating short link for URL {}: {}", originalUrl, e.getMessage());
            throw new BaseException(e.getMessage(), ResponseCode.SHORT_LINK_GENERATE_ERROR);
        }
    }

    private void storeShortLinkMapping(String shortLink, String originalUrl) {
        //todo：存数据库
        shortLinkCache.put(shortLink, originalUrl);
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
            if(!BloomFilterContains(shortLink)) {
                log.warn("Short link {} not found in Bloom filter", shortLink);
                throw new BaseException("Short link not found", ResponseCode.SHORT_LINK_QUERY_ERROR);
            }
            String originalUrl = queryShortLink(shortLink);
            if(originalUrl == null){
                log.warn("Short link {} not found", shortLink);
                throw new BaseException("Short link not found", ResponseCode.SHORT_LINK_QUERY_ERROR);
            }
            shortLinkCache.put(shortLink, originalUrl);
            log.info("Short link {} maps to original URL {}", shortLink, originalUrl);
            return originalUrl;
        } catch (Exception e) {
            log.error("Error querying short link {}: {}", shortLink, e.getMessage());
            throw new BaseException(e.getMessage(), ResponseCode.SHORT_LINK_QUERY_ERROR);
        }
    }

    private boolean BloomFilterContains(String shortLink) {
        return bloomFilter.mightContain(shortLink);
    }

    private String queryShortLink(String shortLink) throws Exception{
        String originalUrl = getFromCache(shortLink);
        if(originalUrl != null) {
            return originalUrl;
        }
        originalUrl = getFromDatabase(shortLink);
        return originalUrl;
    }

    private String getFromDatabase(String shortLink) {
        // todo: implement database retrieval logic
        return "";
    }

    private String getFromCache(String shortLink) {
        return shortLinkCache.getIfPresent(shortLink);
    }
}
