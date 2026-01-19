package com.oBrway.shortLink.core.MapperTest;

import com.oBrway.shortLink.core.model.ShortLinkMappingInfo;
import org.junit.jupiter.api.Test;
import com.oBrway.shortLink.core.respository.sql.ShortLinkMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@MapperScan("com.oBrway.shortLink.core.respository.sql")
public class shortLinkMapperTest {

    @Autowired(required = false)
    private ShortLinkMapper shortLinkMapper;

    @Test
    public void getOriginalLinkByShortLinkTest() {
        String originalLink = shortLinkMapper.getOriginalLinkByShortLink("test");

        assertEquals("testtest", originalLink);
    }

    @Test
    public void getOriginalLinkByIdTest() {
        ShortLinkMappingInfo originalLink = shortLinkMapper.getOriginalLinkById(1L);
        assertEquals("testtest", originalLink.getOriginalLink());
    }
}
