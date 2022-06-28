package com.lan5th.blog.service;

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
    Integer getBlogCommentCount(Long blogId);
    
    /**
     * 获取留言计数
     * @return
     */
    Integer getReplyCount();
}
