package com.oBrway.shortLink.core.respository;

import com.oBrway.shortLink.common.enums.ResponseCode;
import com.oBrway.shortLink.common.exception.BaseException;
import com.oBrway.shortLink.core.model.ShortLinkMappingInfo;
import com.oBrway.shortLink.core.respository.bloomFilter.RedisBloomFilter;
import com.oBrway.shortLink.core.respository.cache.CacheLayer;
import com.oBrway.shortLink.core.respository.cache.LocalLRUCacheLayer;
import com.oBrway.shortLink.core.respository.cache.RedisCacheLayer;
import com.oBrway.shortLink.core.respository.sql.ShortLinkMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据库访问对象，布隆过滤器逻辑、缓存逻辑都在这里实现
 */
@Component
public class ShortLinkDAO {

    private CacheLayer localCache = new LocalLRUCacheLayer();

    private CacheLayer redisCache = new RedisCacheLayer();

    @Autowired
    private RedisBloomFilter redisBloomFilter;

    @Getter
    @Autowired
    private ShortLinkMapper shortLinkMapper;

    public ShortLinkDAO() {
    }

    /**
     * 存储逻辑：
     * 先存数据库，再存缓存，再存布隆过滤器，再存本地缓存
     * @param id
     * @param shortLink
     * @param originalUrl
     */
    public void storeShortLinkMapping(long id, String shortLink, String originalUrl) throws Exception {
        try{
            shortLinkMapper.insertShortLinkMapping(id, shortLink, originalUrl);
            ShortLinkMappingInfo info = new ShortLinkMappingInfo(id, shortLink, originalUrl);
            redisCache.put(id, info);
            redisBloomFilter.add(shortLink);
            localCache.put(id, info);
        } catch (Exception e) {
            throw new BaseException(e.getMessage(), ResponseCode.SHORT_LINK_STORE_ERROR);
        }
    }

    /**
     * 查询逻辑：
     * 查本地缓存->布隆过滤器->redis缓存->数据库
     * @param id
     * @return
     */
    public String queryShortLink(long id, String shortLink) {
        try{
            // Local Cache
            ShortLinkMappingInfo info = localCache.get(id);
            if (info != null) {
                return info.getOriginalLink();
            }
            // Bloom Filter
            if(!redisBloomFilter.contains(shortLink)){
                return null;
            }
            // Redis Cache
            info = redisCache.get(id);
            if( info != null) {
                localCache.put(id, info);
                return info.getOriginalLink();
            }
            // Database
            String oUrl = shortLinkMapper.getOriginalLinkById(id);
            if( oUrl != null && !oUrl.isEmpty()) {
                info = new ShortLinkMappingInfo(id, shortLink, oUrl);
                redisCache.put(id, info);
                localCache.put(id, info);
                return oUrl;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
