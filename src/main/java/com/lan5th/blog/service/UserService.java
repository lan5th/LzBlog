package com.lan5th.blog.service;

import com.lan5th.blog.pojo.User;
import com.lan5th.blog.pojo.UserAuth;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lan5th
 * @date 2022/6/28 10:00
 */
public interface UserService {
    /**
     * 用户登录认证，返回的是token
     * @param userId
     * @param password
     * @return
     */
    String auth(String userId, String password);
    
    UserAuth getLoginUser(String userId);
    
    User getUserInfo(String userId);
    
    void extendLogin(String userId);
    
    void logout(String userId);
    
    /**
     * qq登录的回调验证
     * @param request
     * @return
     */
    String qqAuth(HttpServletRequest request);
}
