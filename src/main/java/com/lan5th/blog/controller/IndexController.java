package com.lan5th.blog.controller;

import com.lan5th.blog.anotation.RequireToken;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;
import com.lan5th.blog.pojo.User;
import com.lan5th.blog.service.BlogDetailsService;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.service.CommentService;
import com.lan5th.blog.service.TagsService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Controller
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private BlogsService blogsService;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogDetailsService blogDetailsService;
    
    @RequestMapping(path = "/tags", method = RequestMethod.GET)
    public ModelAndView tags() {
        return new ModelAndView("html/tags");
    }
    
    /**
     * 杂项，汇总首页显示的各项信息
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/topBlog", method = RequestMethod.GET)
    public JsonObject topBlog() {
        JsonObject res = new JsonObject();
        //置顶博客
        List<BlogDetail> tops = blogsService.getTopBlogs();
        if (tops != null && tops.size() != 0) {
            res.put("list", tops);
        }
        //在展示置顶博客的时候顺带将计数数据带出去
        ArrayList<Integer> countList = new ArrayList<>();
        countList.add(blogsService.getTotalCount());
        countList.add(tagsService.getAllTagCount());
        countList.add(commentService.getBlogCommentCount(null));
        countList.add(commentService.getReplyCount());
        res.put("countList", countList);
        //分页博客总计数
        res.put("totalCount", blogsService.getTotalCount());
        return res;
    }
    
    @ResponseBody
    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public JsonObject indexList(@RequestParam(required = false) Map<String, Object> params) {
        Integer pageNum = Integer.valueOf((String)params.get("pageNum"));
        Integer pageSize = Integer.valueOf((String)params.get("pageSize"));
        String tagId = (String)params.get("tagId");
        JsonObject res = new JsonObject();
        if (pageNum != null && pageSize != null) {
            List<BlogDetail> list;
            if (tagId != null) {
                list = tagsService.getBlogList4Tag(pageNum, pageSize, Long.valueOf(tagId));
            } else {
                list = blogsService.getIndexBlogs(pageNum, pageSize);
            }
            if (list != null && list.size() != 0) {
                res.put("list", list);
            }
        }
        return res;
    }
    
    @ResponseBody
    @RequestMapping(path = "/allTags", method = RequestMethod.GET)
    public JsonObject allTags(@RequestParam Map<String, Object> params) {
        JsonObject res = new JsonObject();
        List<Tag> allTags = tagsService.getAllTags();
        res.put("allTags", allTags);
        String blogId = (String)params.get("blogId");
        if (blogId != null && blogId.length() > 0) {
            //createBlog.html用到
            res.put("oldBlog", blogDetailsService.getBlogDetail(blogId));
        } else {
            //tags.html用到
            res.put("defaultList", tagsService.getBlogList4Tag(1, 10, allTags.get(0).getId()));
        }
        return res;
    }
    
    @RequireToken
    @ResponseBody
    @RequestMapping(path = "/setTop/{id}", method = RequestMethod.POST)
    public JsonObject setTop(@PathVariable("id") String blogId) {
        JsonObject res = new JsonObject();
        blogsService.newTopBlog(blogId);
        return res;
    }
    
    @RequireToken
    @ResponseBody
    @RequestMapping(path = "/cancelTop/{id}", method = RequestMethod.POST)
    public JsonObject cancelTop(@PathVariable("id") String blogId) {
        JsonObject res = new JsonObject();
        blogsService.cancelTop(blogId);
        return res;
    }
    
    @RequireToken
    @ResponseBody
    @RequestMapping(value = "/updateTag", method = RequestMethod.POST)
    public JsonObject updateTag(@RequestParam Map<String, Object> params) {
        String id = (String) params.get("id");
        String tagName = (String) params.get("tagName");
        tagsService.updateTag(id, tagName);
        return new JsonObject();
    }
}
