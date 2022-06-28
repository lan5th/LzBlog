package com.lan5th.blog.dao;

import com.lan5th.blog.pojo.User;
import com.lan5th.blog.pojo.UserAuth;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Mapper
@Component
public interface UserMapper {
    User getById(long id);
    
    List<User> getByIds(List<Long> ids);
    
    void update(User user);
    
    void save(User user);
    
    void deleteByIds(List<Long> ids);
    
    /**
     * 从user_auth表中获取认证信息
     * @param Id
     * @return
     */
    UserAuth auth(long Id);
}
