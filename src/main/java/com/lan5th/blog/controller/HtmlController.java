package com.lan5th.blog.controller;

import com.lan5th.blog.pojo.User;
import com.lan5th.blog.utils.UserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Controller
@RequestMapping("/html")
public class HtmlController {
    @RequestMapping("/head.html")
    public String head() {
        return "html/particle/head";
    }
    
    @RequestMapping("/foot.html")
    public String foot() {
        return "html/particle/foot";
    }
    
    @RequestMapping("/layout.html")
    public String layout() {
        return "html/particle/layout";
    }
    
    @RequestMapping("/side.html")
    public String side() {
        return "html/particle/side";
    }
    
    @RequestMapping("/404")
    public String errorHandle() { return "html/404"; }
}
