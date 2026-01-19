package com.oBrway.shortLink.core.respository.sql;

import com.oBrway.shortLink.core.model.ShortLinkMappingInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.Time;

@Mapper
public interface ShortLinkMapper {
    @Insert("insert into url.url_mapping (id,short_url,long_url,expire_time)"+
            "values(#{id},#{shortLink},#{originalLink}),#{expireTime})")
    long insertShortLinkMapping(long id, String shortLink, String originalLink, Time expireTime);

    @Select("select long_url from url.url_mapping where short_url = #{shortLink} limit 1")
    String getOriginalLinkByShortLink(String shortLink);

    @Select("Select * from url.url_mapping where id = #{id} limit 1")
    ShortLinkMappingInfo getOriginalLinkById(long id);

    @Select("Delete from url.url_mapping where id = #{id}")
    void deleteById(long id);
}
