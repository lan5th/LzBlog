package com.lan5th.blog.controller;

import com.alibaba.druid.util.StringUtils;
import com.lan5th.blog.anotation.RequireToken;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;
import com.lan5th.blog.service.BlogDetailsService;
import com.lan5th.blog.service.CommentService;
import com.lan5th.blog.service.TagsService;
import com.lan5th.blog.utils.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Slf4j
@Controller
@RequestMapping("/details")
public class BlogDetailsController {
    @Resource
    private BlogDetailsService blogDetailsService;
    @Resource
    private TagsService tagsService;
    @Resource
    private CommentService commentService;

    @RequestMapping(value = "/{articleId}", method = RequestMethod.GET)
    public ModelAndView details(@PathVariable("articleId") String articleId) {
        ModelAndView mav = new ModelAndView("html/detail");
        if (StringUtils.isNumber(articleId)) {
            BlogDetail detail = blogDetailsService.getBlogDetail(articleId);
            if (detail != null) {
                addBlogViews(articleId);
                detail.setViews(detail.getViews() + 1);
                //设置参数
                mav.addObject("detail", detail);
                mav.addObject("articleContent", blogDetailsService.getContent(articleId));
                mav.addObject("commentTotal", commentService.getBlogCommentCount(articleId));
                
                return mav;
            }
        } else {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            StringBuffer url = attributes.getRequest().getRequestURL();
            log.error("错误的请求url: " + url);
        }
        return new ModelAndView("html/404");
    }
    
    @RequestMapping(value = "/update/{blogId}", method = RequestMethod.GET)
    public ModelAndView updatePage(@PathVariable("blogId") String blogId){
        ModelAndView mav = new ModelAndView("html/createBlog");
        mav.addObject("blogId", blogId);
        return mav;
    }
    
    /**
     * 这里的执行是在FileController.upload()之后
     * @param params
     * @return
     */
    @Transactional
    @RequireToken
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject saveBlog(@RequestParam Map<String, Object> params){
        JsonObject res = new JsonObject();
        String blogId = (String)params.get("blogId");
        String title = (String)params.get("title");
        String tagId = (String)params.get("tagId");
        String shortContent = (String)params.get("shortContent");
    
        //先检验是否已经上传文件
        BlogDetail detail = new BlogDetail();
        if (blogId == null || blogId.length() == 0) {
            res.setStatus(false);
            res.setMessage("新建博客时必须先上传文件!");
            return res;
        }
        
        //新创建的tag
        String newTagName = (String)params.get("newTagName");
        if ((tagId == null || tagId.length() == 0) && newTagName != null && newTagName.length() > 0) {
            Tag newTag = new Tag();
            newTag.setIdIfNew();
            newTag.setTagName(newTagName);
            tagsService.saveTag(newTag);
            tagId = newTag.getId().toString();
        }
        
        detail.setId(Long.parseLong(blogId));
        detail.setIdIfNew();
        if (title != null && title.length() > 0)
            detail.setBlogName(title);
        if (tagId != null && tagId.length() > 0)
            detail.setTagId(Long.parseLong(tagId));
        if (shortContent != null && shortContent.length() > 0)
            detail.setShortContent(shortContent);
        detail.setDeleted(false);
        
        //已经由FileController.upload()保存过新的BlogDetail了，这里只能是update
        blogDetailsService.updateBlog(detail);
        return res;
    }
    
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createPage(){
        ModelAndView mav = new ModelAndView("html/createBlog");
        return mav;
    }
    
    @Transactional
    @RequireToken
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public JsonObject deleteBlog(@PathVariable("id") String blogId) {
        JsonObject res = new JsonObject();
        String msg = blogDetailsService.deleteBlog(blogId);
        if (msg != null) {
            res.setStatus(false);
            res.setMessage(msg);
        }
        return res;
    }
    
    //增加博客阅读人数专用方法
    private void addBlogViews(String blogId) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        
        String ip = request.getRemoteAddr();
        //获取代理ip
        String headerIP = request.getHeader("x-real-ip");
        if (headerIP == null || "".equals(headerIP) || "null".equals(headerIP)){
            headerIP = request.getHeader("x-forwarded-for");
        }
        
        if (headerIP !=null && !"".equals(headerIP) && !"null".equals(headerIP)){
            //如果使用多重代理，拿到的x-forwarded-for可能是一连串用" "分割的ip，这里取第一个为真实ip
            headerIP = headerIP.split(" ")[0];
            ip = headerIP;
        }
        blogDetailsService.increaseView(blogId, ip);
    }
}
