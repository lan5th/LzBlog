package com.lan5th.blog.controller;

import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;
import com.lan5th.blog.pojo.User;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.service.CommentService;
import com.lan5th.blog.service.TagsService;
import com.lan5th.blog.utils.JsonObject;
import com.lan5th.blog.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    
    @RequestMapping(method = RequestMethod.GET, path = "/tags")
    public ModelAndView tags() {
        return new ModelAndView("html/tags");
    }
    
    /**
     * 杂项，汇总首页显示的各项信息
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/topBlog")
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
        //展示页面是带上登录用户信息
        User user = UserUtil.getCurrentUser();
        if (user != null) {
            res.put("user", user);
        }
        //分页博客总计数
        res.put("totalCount", blogsService.getTotalCount());
        return res;
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/list")
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
    @RequestMapping(method = RequestMethod.GET, path = "/allTags")
    public JsonObject allTags() {
        JsonObject res = new JsonObject();
        List<Tag> allTags = tagsService.getAllTags();
        res.put("allTags", allTags);
        if (allTags != null && allTags.size() != 0) {
            res.put("defaultList", tagsService.getBlogList4Tag(1, 10, allTags.get(0).getId()));
        }
        return res;
    }
}
