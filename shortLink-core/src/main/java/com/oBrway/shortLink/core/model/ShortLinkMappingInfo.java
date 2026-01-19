package com.oBrway.shortLink.core.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ShortLinkMappingInfo {
    private long id;
    private String shortLink;
    private String originalLink;
    private Timestamp expireTime;

    public ShortLinkMappingInfo(long id, String shortLink, String originalLink, Timestamp expireTime) {
        this.id = id;
        this.shortLink = shortLink;
        this.originalLink = originalLink;
        this.expireTime = expireTime;
    }
}
