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

import java.sql.Time;

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

    private final long EXPIRE_TIME = 30L * 24 * 60 * 60 * 1000; // 30天后过期

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
            Time expireTime = new Time(System.currentTimeMillis() + EXPIRE_TIME); // 30天后过期
            shortLinkMapper.insertShortLinkMapping(id, shortLink, originalUrl, expireTime);
            ShortLinkMappingInfo info = new ShortLinkMappingInfo(id, shortLink, originalUrl, expireTime);
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
                updateExpireTime(id);
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
                updateExpireTime(id);
                return info.getOriginalLink();
            }
            // Database
            ShortLinkMappingInfo oUrlInfo = shortLinkMapper.getOriginalLinkById(id);
            String oUrl = oUrlInfo != null ? oUrlInfo.getOriginalLink() : null;
            Time expireTime = oUrlInfo != null ? oUrlInfo.getExpireTime() : null;
            if(expireTime == null || expireTime.getTime() < System.currentTimeMillis()) {
                // 链接已过期, 由于业务需求，记录不删除
                return null;
            }
            if( oUrl != null && !oUrl.isEmpty()) {
                info = new ShortLinkMappingInfo(id, shortLink, oUrl, oUrlInfo.getExpireTime());
                redisCache.put(id, info);
                localCache.put(id, info);
                return oUrl;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void updateExpireTime(long key){
        redisCache.updateExpireTime(key);
    }
}
