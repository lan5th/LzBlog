package com.lan5th.blog.service;

import com.lan5th.blog.pojo.Comment;

import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 22:37
 */
public interface CommentService {
    /**
     * 获取博客评论计数
     * blogId:
     *      不为null:     查询某个具体博客的评论计数
     *      为null:      查询所有博客评论计数
     * @param blogId
     * @return
     */
    int getBlogCommentCount(String blogId);
    
    /**
     * 获取留言计数
     * @return
     */
    int getReplyCount();
    
    /**
     * 获取一个博客的评论列表
     */
    List<Comment> getComment4Blog(Integer pageNum, Integer pageSize, String blogId);
    
    /**
     * 获取留言板列表
     */
    List<Comment> getReplyList(Integer pageNum, Integer pageSize);
    
    void saveComment(String content, String blogId, String replyTo);
    
    void delComment(String commentId, String blogId);
    
    Comment getComment(String id);
    
    List<Comment> getCommentByPage(Integer pageNum, Integer pageSize, Long blogId);
    
    void cleanCommentCache(String blogId);
}
