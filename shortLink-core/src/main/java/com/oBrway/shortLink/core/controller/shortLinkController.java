package com.oBrway.shortLink.core.controller;

import com.oBrway.shortLink.core.respository.sql.ShortLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class shortLinkController {
    @Autowired(required = false)
    private ShortLinkMapper shortLinkMapper;

    @GetMapping("/getOriginalLink")
    public String getOriginalLinkByShortLinkTest() {
        return shortLinkMapper.getOriginalLinkByShortLink("test");
    }
}
