package com.lan5th.blog.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lan5th.blog.dao.UserMapper;
import com.lan5th.blog.pojo.User;
import com.lan5th.blog.pojo.UserAuth;
import com.lan5th.blog.service.UserService;
import com.lan5th.blog.utils.RedisUtil;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lan5th
 * @date 2022/6/28 10:01
 */
@Service
public class UserServiceImpl implements UserService {
    private static final String LOGIN_KEY = "token-";
    private static final String USER_INFO_KEY = "userInfo-";
    
    //qq登录统一密码
    private static final String QQ_LOGIN_PASS = "qqLogin";
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
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
        
            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                System.out.print("没有获取到响应参数");
            } else {
                String accessToken = accessTokenObj.getAccessToken();
//                long tokenExpireIn = accessTokenObj.getExpireIn();
//
//                request.getSession().setAttribute("demo_access_token", accessToken);
//                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));
            
                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                String openID = openIDObj.getUserOpenID();
            
//                request.getSession().setAttribute("demo_openid", openID);
                // 利用获取到的accessToken 去获取当前用户的openid --------- end
            
            
                //    out.println("<p> start -----------------------------------利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息 ---------------------------- start </p>");
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                
                
                if (userInfoBean.getRet() == 0) {
                    User user = userMapper.getById(Long.parseLong(openID));
                    if (user == null) {
                        user = new User();
                        user.setId(Long.parseLong(openID));
                        user.setAvatarUrl(userInfoBean.getAvatar().getAvatarURL30());
                        user.setIsAdmin(false);
                        //userInfo
                        userMapper.save(user);
                        UserAuth auth = new UserAuth();
                        auth.setId(Long.parseLong(openID));
                        auth.setPassword(QQ_LOGIN_PASS);
                        //userAuth
                        userMapper.saveAuth(auth);
                    }
                    //获取本地token
                    String sign = JWT.create().withAudience(String.valueOf(user.getId()), "").sign(Algorithm.HMAC256(QQ_LOGIN_PASS));
                    return sign;
                } else {
                    System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                }
            }
        } catch (QQConnectException e) {
            System.out.println("未知错误QQConnectException");
        }
        return null;
    }
}
