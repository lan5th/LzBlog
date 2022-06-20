package com.lan5th.blog.controller;

import com.lan5th.blog.service.BlogDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/details")
public class BlogDetailsController {
    @Autowired
    BlogDetailsService blogDetailsService;

    @RequestMapping(value = "/{articleId}", method = RequestMethod.GET)
    public String details(@PathVariable("articleId") long articleId) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        //设置参数
        request.setAttribute("articleName", blogDetailsService.getBlogDetail(articleId).getBlogName());
        request.setAttribute("articleContext", blogDetailsService.getContent(articleId));
        //视图名交给Thymeleaf解析
        return "html/detail";
    }
}
