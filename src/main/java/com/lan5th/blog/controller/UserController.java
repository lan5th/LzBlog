package com.lan5th.blog.controller;

import com.lan5th.blog.service.UserService;
import com.lan5th.blog.utils.JsonObject;
import com.lan5th.blog.utils.UserUtil;
import com.qq.connect.QQConnectException;
import com.qq.connect.oauth.Oauth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author lan5th
 * @date 2022/6/28 8:40
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage() {
        return new ModelAndView("html/login");
    }
    
    /**
     * 管理员登录
     * @param params
     * @return
     */
    @RequestMapping(path = "/auth", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject auth(@RequestParam Map<String, Object> params) {
        JsonObject res = new JsonObject();
        String userId = (String)params.get("userId");
        String password = (String)params.get("password");
        String token = userService.auth(userId, password);
        if (token != null) {
            res.put("token", token);
        }
        return res;
    }
    
    /**
     * QQ登录
     * @param request
     * @param response
     */
    @RequestMapping(value = "/qqLogin", method = RequestMethod.GET)
    public void LoginByQQ(HttpServletRequest request, HttpServletResponse response){
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException | IOException e) {
            log.error("QQ登录重定向报错", e);
        }
    }
    
    /**
     * QQ登录的回调地址
     * @param request
     * @return
     */
    @RequestMapping(value = "/qqAuth", method = RequestMethod.GET)
    public ModelAndView qqAuth(HttpServletRequest request,String code) {
        ModelAndView mav;
        String token = userService.qqAuth(request);
        if (token != null) {
            mav = new ModelAndView("html/index");
            mav.addObject("token", token);
        } else {
            mav = new ModelAndView("html/login");
            mav.addObject("errMsg", "内部错误，登录失败!");
        }
        return mav;
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public JsonObject logout() {
        UserUtil.logout();
        return new JsonObject();
    }
}
