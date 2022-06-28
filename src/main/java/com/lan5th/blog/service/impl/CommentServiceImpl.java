package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.CommentMapper;
import com.lan5th.blog.service.CommentService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lan5th
 * @date 2022/6/23 22:40
 */
@Service
public class CommentServiceImpl implements CommentService {
    //缓存标识
    private static final String COMMENT_COUNT_KEY = "commentCount";
    private static final String REPLY_COUNT_KEY = "replyCount";
    //过期时间
    private static final int EXPIRE_TIME = 10;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public Integer getBlogCommentCount(Long blogId) {
        Integer commentCount;
        if (blogId == null) {
            //所有博客评论计数
            if ((commentCount = (Integer) redisUtil.get(COMMENT_COUNT_KEY)) == null) {
                commentCount = commentMapper.getCommentCount(null);
                redisUtil.set(COMMENT_COUNT_KEY, commentCount, EXPIRE_TIME);
            }
        } else {
            //单个博客评论计数
            if ((commentCount = (Integer) redisUtil.get(COMMENT_COUNT_KEY + "-" + blogId)) == null) {
                commentCount = commentMapper.getCommentCount(blogId);
                redisUtil.set(COMMENT_COUNT_KEY + "-" + blogId, commentCount, EXPIRE_TIME);
            }
        }
        return commentCount;
    }
    
    @Override
    public Integer getReplyCount() {
        Integer commentCount;
        if ((commentCount = (Integer) redisUtil.get(REPLY_COUNT_KEY)) == null) {
            commentCount = commentMapper.getCommentCount(-1L);
            redisUtil.set(REPLY_COUNT_KEY, commentCount, EXPIRE_TIME);
        }
        return commentCount;
    }
}
