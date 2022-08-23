package com.lan5th.blog.dao;

import com.lan5th.blog.pojo.Link;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Entity com.lan5th.blog.pojo.Links
 */
@Mapper
@Component
public interface LinksMapper {

    int deleteById(Long id);

    int save(Link record);

//    int insertSelective(Link record);

    Link getById(Long id);

    int update(Link record);

//    int updateByPrimaryKey(Link record);
    
    List<Link> getLinkByType(Integer type);
}
