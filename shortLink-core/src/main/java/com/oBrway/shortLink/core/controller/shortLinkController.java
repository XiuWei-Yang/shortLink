package com.oBrway.shortLink.core.controller;

import com.oBrway.shortLink.core.service.ShortLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/shortLink")
public class shortLinkController {
    @Autowired
    ShortLink shortLinkService;

    @GetMapping("/getOriginalLink")
    @ResponseBody
    public String getOriginalLinkByShortLink(String shortLink) throws Exception {
        try{
            return shortLinkService.getOriginalUrl(shortLink);
        } catch (Exception e) {
            throw new Exception("Error retrieving original link: " + e.getMessage());
        }
    }

    @GetMapping("/generateShortLink")
    @ResponseBody
    public String generateShortLink(String originalLink) {
        try{
            return shortLinkService.generateAndStoreShortLink(originalLink);
        } catch (Exception e) {
            return "Error generating short link: " + e.getMessage();
        }
    }
}
