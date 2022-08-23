package com.lan5th.blog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lan5th.blog.pojo.User;
import com.lan5th.blog.pojo.UserAuth;
import com.lan5th.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * user登录工具类
 * @author lan5th
 * @date 2022/6/28 11:00
 */
//这里Component主要是为了注入userService
@Component
public class UserUtil {
    private static UserService userService;
    //存储userId的ThreadLocal
    private static final ThreadLocal<String> userIdContext = new ThreadLocal<>();
    
    @Autowired
    public void setUserService(UserService userService) {
        UserUtil.userService = userService;
    }
    
    /**
     * 验证token，并封装userIdContext
     * 已登录返回true，未登录返回false
     * @return
     */
    public static boolean verifyToken() {
        ServletRequestAttributes servletRequestAttributes =  (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("token");
    
        // 执行认证
        if (token == null || "null".equals(token)) {
            return false;
        }
        // 获取 token 中的 user id
        String userId;
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            //解码
            userId = decodedJWT.getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new RuntimeException("401");
        }
        UserAuth loginUser = userService.getLoginUser(userId);
        if (loginUser == null) {
            return false;
        }
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(loginUser.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        //延长登录时间
        userService.extendLogin(userId);
        //验证完成设置ThreadContext，方便后续存取
        userIdContext.set(userId);
        return true;
    }
    
    /**
     * 获取当前登录用户
     * 在执行完verifyToken()之后才能正常获取
     * @return
     */
    public static User getCurrentUser() {
        String userId = userIdContext.get();
        if (userId == null) {
            return null;
        }
        return userService.getUserInfo(userId);
    }
    
    /**
     * 结束时清除userIdContext的对应缓存，防止内存泄露
     */
    public static void cleanIdCache() {
        userIdContext.remove();
    }
    
    /**
     * 登出
     * @return
     */
    public static void logout() {
        userService.logout(userIdContext.get());
    }
}
