package com.lan5th.blog.dao;

import com.lan5th.blog.pojo.UserAuth;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserAuthMapper {
    UserAuth getById(long id);
    
    void update(UserAuth userAuth);
    
    void save(UserAuth userAuth);
    
    void deleteByIds(List<Long> ids);
}
