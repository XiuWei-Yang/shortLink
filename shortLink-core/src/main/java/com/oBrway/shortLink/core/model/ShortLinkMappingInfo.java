package com.oBrway.shortLink.core.model;

import lombok.Data;

@Data
public class ShortLinkMappingInfo {
    private long id;
    private String shortLink;
    private String originalLink;

    public ShortLinkMappingInfo(long id, String shortLink, String originalLink) {
        this.id = id;
        this.shortLink = shortLink;
        this.originalLink = originalLink;
    }
}
