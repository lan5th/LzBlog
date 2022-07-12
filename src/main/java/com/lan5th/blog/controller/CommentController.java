package com.lan5th.blog.controller;

import com.lan5th.blog.anotation.RequireToken;
import com.lan5th.blog.pojo.Comment;
import com.lan5th.blog.service.CommentService;
import com.lan5th.blog.utils.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author lan5th
 * @date 2022/7/10 9:36
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView replyPage() {
        ModelAndView mav = new ModelAndView("html/comment");
        mav.addObject("commentTotal", commentService.getReplyCount());
        return mav;
    }
    
    @ResponseBody
    @RequestMapping(value = "/getComment", method = RequestMethod.GET)
    public JsonObject getComment(@RequestParam Map<String, Object> params) {
        JsonObject res = new JsonObject();
        Integer pageNum = Integer.valueOf((String)params.get("pageNum"));
        Integer pageSize = Integer.valueOf((String)params.get("pageSize"));
        String blogId = (String) params.get("blogId");
        
        List<Comment> commentList = null;
        if (blogId != null) {
            commentList = commentService.getComment4Blog(pageNum, pageSize, blogId);
        } else {
            commentList = commentService.getReplyList(pageNum, pageSize);
        }
        
        res.put("commentList", commentList);
        return res;
    }
    
    @Transactional
    @RequireToken(requireAdmin = false)
    @ResponseBody
    @RequestMapping(value = "/saveComment", method = RequestMethod.POST)
    public JsonObject saveComment(@RequestParam Map<String, Object> params) {
        JsonObject res = new JsonObject();
        String content = (String)params.get("content");
        
        String blogId = (String)params.get("blogId");
        String replyTo = (String)params.get("replyTo");
        
        commentService.saveComment(content, blogId, replyTo);
        return res;
    }
    
    @Transactional
    @RequireToken
    @ResponseBody
    @RequestMapping(value = "/delComment", method = RequestMethod.DELETE)
    public JsonObject deleteComment(@RequestParam Map<String, Object> params) {
        JsonObject res = new JsonObject();
        String commentId = (String)params.get("commentId");
        String blogId = (String)params.get("blogId");
        
        commentService.delComment(commentId, blogId);
        return res;
    }
}
