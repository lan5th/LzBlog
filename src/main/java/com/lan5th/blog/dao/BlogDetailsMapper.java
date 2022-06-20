package com.lan5th.blog.dao;

import com.lan5th.blog.pojo.BlogDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

//其实只需要一个@Mapper就足够了，添加@Component是解决idea报错
@Mapper
@Component
public interface BlogDetailsMapper {
    BlogDetail getById(long id);

    void update(BlogDetail blogDetail);

    void insert(BlogDetail blogDetail);

    void deleteById(long id);

    void remove(long id);
}
