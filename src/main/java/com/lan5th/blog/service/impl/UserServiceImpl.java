package com.lan5th.blog.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lan5th.blog.dao.UserAuthMapper;
import com.lan5th.blog.dao.UserMapper;
import com.lan5th.blog.pojo.User;
import com.lan5th.blog.pojo.UserAuth;
import com.lan5th.blog.service.UserService;
import com.lan5th.blog.utils.RedisUtil;
import com.lan5th.blog.utils.UIDUtil;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author lan5th
 * @date 2022/6/28 10:01
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private static final String LOGIN_KEY = "token-";
    private static final String USER_INFO_KEY = "userInfo-";
//    private static final Long EXPIRE_TIME = 30 * 60 * 1000L;
    
    //qq登录统一密码
    private static final String QQ_LOGIN_PASS = "qqLogin";
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAuthMapper userAuthMapper;
    @Resource
    private RedisUtil redisUtil;
    
    @Override
    public String auth(String userId, String password) {
        if (userId != null && password != null) {
            if (!userId.isEmpty() && !password.isEmpty()) {
                UserAuth byId = userAuthMapper.auth(Long.parseLong(userId));
                if (byId != null && byId.getPassword().equals(password)) {
                    log.info(byId.getId() + " 进行了管理员登录");
                    //签发Token
                    String sign = JWT.create().withAudience(String.valueOf(byId.getId()))
//                            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                            .withIssuedAt(new Date())
                            .sign(Algorithm.HMAC256(byId.getPassword()));
                    //30min过期
                    redisUtil.set(LOGIN_KEY + byId.getId().toString(), byId, 1, TimeUnit.HOURS);
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
            redisUtil.set(LOGIN_KEY + userId, loginUser, 1, TimeUnit.HOURS);
        }
    }
    
    @Override
    public void logout(String userId) {
        redisUtil.delete(LOGIN_KEY + userId);
    }
    
    @Override
    public String qqAuth(HttpServletRequest request) {
        try {
            //在这里之前必须先执行new Oauth().getAuthorizeURL(request)方法,否则里面获取不到request中的qq_connect_state
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            //这里sdk会自己再用code发一次请求来获取accessToken
            String accessToken = accessTokenObj.getAccessToken();
            if ("".equals(accessToken)) {
                log.error("没有获取到响应参数");
            } else {
                OpenID openIDObj =  new OpenID(accessToken);
                String openID = openIDObj.getUserOpenID();
            
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                
                
                if (userInfoBean.getRet() == 0) { //状态码为0表示成功
                    User user = userMapper.getByOpenId(openID);
                    UserAuth auth = null;
                    if (user == null) {
                        user = new User();
                        user.setId(UIDUtil.getNewId());
                        user.setName(userInfoBean.getNickname());
                        user.setOpenId(openID);
                        user.setAvatarUrl(userInfoBean.getAvatar().getAvatarURL30());
                        user.setIsAdmin(false);
                        //userInfo
                        userMapper.save(user);
                        auth = new UserAuth();
                        auth.setId(user.getId());
                        auth.setPassword(QQ_LOGIN_PASS);
                        //userAuth
                        userAuthMapper.saveAuth(auth);
                    } else {
                        auth = userAuthMapper.getById(user.getId());
                    }
                    log.info(user.getId() + " 进行了qq登录");
                    //获取本地token
                    String sign = JWT.create().withAudience(String.valueOf(user.getId()))
//                            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                            .withIssuedAt(new Date())
                            .sign(Algorithm.HMAC256(QQ_LOGIN_PASS));
                    redisUtil.set(LOGIN_KEY + user.getId(), auth, 1, TimeUnit.HOURS);
                    return sign;
                } else {
                    log.warn("未能正确获取到用户信息，原因是： " + userInfoBean.getMsg());
                }
            }
        } catch (QQConnectException e) {
            log.error("未知错误QQConnectException", e);
        }
        return null;
    }
}
