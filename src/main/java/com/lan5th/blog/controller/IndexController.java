package com.lan5th.blog.controller;

import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.service.TagsService;
import com.lan5th.blog.utils.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
    BlogsService blogsService;
    @Autowired
    TagsService tagsService;
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/topBlog")
    public JsonObject topBlog() throws JSONException {
        JsonObject res = new JsonObject();
        List<BlogDetail> tops = blogsService.getTopBlogs();
        if (tops != null && tops.size() != 0) {
            res.put("list", tops);
        }
        return res;
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/list")
    public JsonObject indexList(@RequestParam(required = false) Map<String, Object> params) throws JSONException {
        Integer pageNum = Integer.valueOf((String)params.get("pageNum"));
        Integer pageSize = Integer.valueOf((String)params.get("pageSize"));
        String tagId = (String)params.get("tagId");
        JsonObject res = new JsonObject();
        if (pageNum != null && pageSize != null) {
            List<BlogDetail> list;
            if (tagId != null) {
                list = tagsService.getBlogList4Tag(pageNum, pageSize, tagId);
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
    @RequestMapping(method = RequestMethod.GET, path = "/total")
    public JsonObject total() throws JSONException {
        JsonObject res = new JsonObject();
        res.put("totalCount", blogsService.getTotalCount());
        return res;
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "/tags")
    public ModelAndView tags() {
        ModelAndView mav = new ModelAndView("html/tags");
        return mav;
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/allTags")
    public JsonObject allTags() throws JSONException {
        JsonObject res = new JsonObject();
        List<Tag> allTags = tagsService.getAllTags();
        res.put("allTags", allTags);
        if (allTags != null && allTags.size() != 0) {
            res.put("defaultList", tagsService.getBlogList4Tag(1, 10, allTags.get(0).getId().toString()));
        }
        return res;
    }
}
