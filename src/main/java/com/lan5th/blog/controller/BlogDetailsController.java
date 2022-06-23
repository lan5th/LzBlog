package com.lan5th.blog.controller;

import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.service.BlogDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Controller
@RequestMapping("/details")
public class BlogDetailsController {
    @Autowired
    BlogDetailsService blogDetailsService;

    @RequestMapping(value = "/{articleId}", method = RequestMethod.GET)
    public ModelAndView details(@PathVariable("articleId") long articleId) {
        ModelAndView mav = new ModelAndView("html/detail");
        BlogDetail detail = blogDetailsService.getBlogDetail(articleId);
        if (detail != null) {
            //设置参数
            mav.addObject("detail", detail);
            mav.addObject("articleContent", blogDetailsService.getContent(articleId));
        } else {
            return new ModelAndView("html/404");
        }
        return mav;
    }
}
