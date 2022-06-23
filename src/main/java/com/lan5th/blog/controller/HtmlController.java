package com.lan5th.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
