package com.lan5th.blog.dao;

import com.lan5th.blog.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface TagMapper {
    Tag getById(long id);
    
    void update(Tag blogDetail);
    
    void save(Tag blogDetail);
    
    void deleteByIds(List<Long> ids);
    
    List<Tag> getAllTags();
}
