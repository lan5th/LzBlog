package com.lan5th.blog.controller;

import com.lan5th.blog.service.UserService;
import com.lan5th.blog.utils.JsonObject;
import com.lan5th.blog.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @author lan5th
 * @date 2022/6/28 8:40
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @RequestMapping(method = RequestMethod.GET, path = "/login")
    public ModelAndView loginPage() {
        return new ModelAndView("/html/login");
    }
    
    @RequestMapping(method = RequestMethod.POST, path = "/auth")
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
    
    @RequestMapping(method = RequestMethod.GET, value = "/logout")
    @ResponseBody
    public JsonObject logout() {
        UserUtil.logout();
        return new JsonObject();
    }
}
