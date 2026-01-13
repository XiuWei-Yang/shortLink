package com.oBrway.shortLink.core.respository.sql;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShortLinkMapper {
    @Insert("insert into shortLink_db.link_mapping (id,short_link,long_link)"+
            "values(#{id},#{shortLink},#{originalLink})")
    long insertShortLinkMapping(long id, String shortLink, String originalLink);

    @Select("select long_link from shortLink_db.link_mapping where short_link = #{shortLink} limit 1")
    String getOriginalLinkByShortLink(String shortLink);

    @Select("Select long_link from shortLink_db.link_mapping where id = #{id} limit 1")
    String getOriginalLinkById(long id);

    @Select("Delete from shortLink_db.link_mapping where id = #{id}")
    void deleteById(long id);
}
