package com.oBrway.shortLink.core.service;

import java.util.concurrent.LinkedBlockingDeque;

import com.oBrway.shortLink.common.enums.ResponseCode;
import com.oBrway.shortLink.common.exception.BaseException;
import com.oBrway.shortLink.core.respository.ShortLinkDAO;
import com.oBrway.shortLink.core.service.Base62.Base62Encoder;
import com.oBrway.shortLink.core.service.IDGenerator.IDGenerator;
import com.oBrway.shortLink.core.service.IDGenerator.Implentation.IDGeneratorInvoker;
import com.oBrway.shortLink.core.config.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Setter
public class ShortLink {
    private LinkedBlockingDeque<Long> idBuffer = new LinkedBlockingDeque<>();

    IDGenerator DistributedIdGenerator;

    Config config = new Config();

    @Getter
    @Autowired
    private ShortLinkDAO shortLinkDAO;

    public ShortLink() {
        DistributedIdGenerator = new IDGeneratorInvoker();
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
            if(idBuffer.isEmpty()) {
                idBuffer = DistributedIdGenerator.getNextID();
            }
            long id = idBuffer.remove();
            String shortLink = Base62Encoder.encode(id);
            shortLinkDAO.storeShortLinkMapping(id, shortLink, originalUrl);
            return shortLink;
        } catch (Exception e) {
            log.error("Error generating short link for URL {}: {}", originalUrl, e.getMessage());
            throw new BaseException(e.getMessage(), ResponseCode.SHORT_LINK_GENERATE_ERROR);
        }
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
