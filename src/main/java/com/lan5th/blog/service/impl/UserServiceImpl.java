package com.lan5th.blog.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lan5th.blog.dao.UserMapper;
import com.lan5th.blog.pojo.User;
import com.lan5th.blog.pojo.UserAuth;
import com.lan5th.blog.service.UserService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author lan5th
 * @date 2022/6/28 10:01
 */
@Service
public class UserServiceImpl implements UserService {
    private static final String LOGIN_KEY = "token-";
    private static final String USER_INFO_KEY = "userInfo-";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public String auth(String userId, String password) {
        if (userId != null && password != null) {
            if (!userId.isEmpty() && !password.isEmpty()) {
                UserAuth byId = userMapper.auth(Long.parseLong(userId));
                if (byId != null && byId.getPassword().equals(password)) {
                    String sign = JWT.create().withAudience(String.valueOf(byId.getId())).sign(Algorithm.HMAC256(byId.getPassword()));
                    //30min过期
                    redisUtil.set(LOGIN_KEY + byId.getId().toString(), byId, 30, TimeUnit.MINUTES);
                    return sign;
                }
            }
        }
        return null;
    }
    
    @Override
    public UserAuth getLoginUser(String userId) {
        return (UserAuth) redisUtil.get(LOGIN_KEY + userId);
    }
    
    @Override
    public User getUserInfo(String userId) {
        if (userId != null && userId.length() != 0) {
            User user = new User();
            if ((user = (User) redisUtil.get(USER_INFO_KEY + userId)) == null) {
                user = userMapper.getById(Long.parseLong(userId));
                redisUtil.set(USER_INFO_KEY + userId, user, 10);
            }
            return user;
        }
        return null;
    }
    
    @Override
    public void extendLogin(String userId) {
        UserAuth loginUser = (UserAuth)redisUtil.get(LOGIN_KEY + userId);
        if (loginUser != null) {
            redisUtil.set(LOGIN_KEY + userId, loginUser, 30, TimeUnit.MINUTES);
        }
    }
    
    @Override
    public void logout(String userId) {
        redisUtil.delete(LOGIN_KEY + userId);
    }
}
