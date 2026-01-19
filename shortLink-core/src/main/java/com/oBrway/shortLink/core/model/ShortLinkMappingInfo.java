package com.oBrway.shortLink.core.model;

import lombok.Data;

import java.sql.Time;

@Data
public class ShortLinkMappingInfo {
    private long id;
    private String shortLink;
    private String originalLink;
    private Time expireTime;

    public ShortLinkMappingInfo(long id, String shortLink, String originalLink, Time expireTime) {
        this.id = id;
        this.shortLink = shortLink;
        this.originalLink = originalLink;
        this.expireTime = expireTime;
    }
}
