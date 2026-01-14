package com.oBrway.shortLink.core.shortLinkTest;

import com.oBrway.shortLink.core.service.IDGenerator.IDGeneratorForTest;
import com.oBrway.shortLink.core.service.ShortLink;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@MapperScan("com.oBrway.shortLink.core.respository.sql")
public class ShortLinkTest {
    @Autowired
    ShortLink shortLink;

    @Test
    public void testCreateAndQueryShortLink() throws Exception {
        shortLink.setDistributedIdGenerator(new IDGeneratorForTest());
        shortLink.getShortLinkDAO().getShortLinkMapper().deleteById(123456789L);
        String originalUrl = "testOriginalUrl";
        String sLink = shortLink.generateAndStoreShortLink(originalUrl);
        String res = shortLink.getOriginalUrl(sLink);
        assertEquals("8M0kX", sLink);
        assertEquals(originalUrl, res);
    }
}
