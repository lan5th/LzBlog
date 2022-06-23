package com.lan5th.blog.dao;

import com.lan5th.blog.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserMapper {
    User getById(long id);
    
    List<User> getByIds(List<Long> ids);
    
    void update(User user);
    
    void save(User user);
    
    void deleteByIds(List<Long> ids);
}
