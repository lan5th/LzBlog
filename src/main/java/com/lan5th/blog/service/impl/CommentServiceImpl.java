package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.CommentMapper;
import com.lan5th.blog.pojo.Comment;
import com.lan5th.blog.pojo.User;
import com.lan5th.blog.service.CommentService;
import com.lan5th.blog.utils.RedisUtil;
import com.lan5th.blog.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 22:40
 */
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    //缓存标识
    private static final String COMMENT_COUNT_KEY = "commentCount";
    private static final String REPLY_COUNT_KEY = "replyCount";
    private static final String COMMENT_KEY = "comment-";
    private static final String REPLY_KEY = "reply-";
    //过期时间
    private static final int EXPIRE_TIME = 5;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CommentMapper commentMapper;
    
    @Override
    public int getBlogCommentCount(String blogId) {
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
                commentCount = commentMapper.getCommentCount(Long.parseLong(blogId));
                redisUtil.set(COMMENT_COUNT_KEY + "-" + blogId, commentCount, EXPIRE_TIME);
            }
        }
        return commentCount;
    }
    
    @Override
    public int getReplyCount() {
        Integer commentCount;
        if ((commentCount = (Integer) redisUtil.get(REPLY_COUNT_KEY)) == null) {
            commentCount = commentMapper.getCommentCount(-1L);
            redisUtil.set(REPLY_COUNT_KEY, commentCount, EXPIRE_TIME);
        }
        return commentCount;
    }
    
    @Override
    public List<Comment> getComment4Blog(Integer pageNum, Integer pageSize, String blogId) {
        if (pageNum < 1 || pageSize < 1)
            return null;
        List<Comment> list = null;
        Object o = redisUtil.get(COMMENT_KEY + blogId + "-" + pageNum + "-" + pageSize);
        if (o == null) {
            list = getCommentByPage(pageNum, pageSize, Long.parseLong(blogId));
            redisUtil.set(COMMENT_KEY + blogId + "-" + pageNum + "-" + pageSize, list, EXPIRE_TIME);
        } else {
            list = (List<Comment>) o;
        }
        return list;
    }
    
    @Override
    public List<Comment> getReplyList(Integer pageNum, Integer pageSize) {
        if (pageNum < 1 || pageSize < 1)
            return null;
        List<Comment> list = null;
        Object o = redisUtil.get(REPLY_KEY + pageNum + "-" + pageSize);
        if (o == null) {
            //blogId为-1表示留言
            list = getCommentByPage(pageNum, pageSize, -1L);
            redisUtil.set(REPLY_KEY + pageNum + "-" + pageSize, list, EXPIRE_TIME);
        } else {
            list = (List<Comment>) o;
        }
        return list;
    }
    
    @Override
    public void saveComment(String content, String blogId, String replyTo) {
        if (content == null || content.length() == 0) {
            return;
        }
        Comment comment = new Comment();
        comment.setIdIfNew();
        comment.setContent(content);
        if (blogId != null) {
            comment.setBlogId(Long.parseLong(blogId));
        } else {
            comment.setBlogId(-1L);
        }
        User currentUser = UserUtil.getCurrentUser();
        comment.setUserId(currentUser.getId());
        log.info(currentUser.getId() + " 发表了评论 " + content + ",回复给" + replyTo);
        if (replyTo != null) {
            comment.setReplyTo(Long.parseLong(replyTo));
        }
        
        commentMapper.save(comment);
        cleanCommentCache(blogId);
    }
    
    @Override
    public void delComment(String commentId, String blogId) {
        if (commentId == null)
            return;
    
        ArrayList<Long> idList = new ArrayList<>();
        idList.add(Long.parseLong(commentId));
        log.info(UserUtil.getCurrentUser().getId() + " 删除了评论,commentId" + commentId);
        commentMapper.deleteByIds(idList);
        cleanCommentCache(blogId);
    }
    
    @Override
    public Comment getComment(String id) {
        if (id == null || id.length() == 0)
            return null;
        Comment comment;
        if ((comment = (Comment) redisUtil.get(COMMENT_KEY + id)) == null) {
            comment = commentMapper.getSingleComment(Long.parseLong(id));
            redisUtil.set(COMMENT_KEY + id, comment, EXPIRE_TIME);
        }
        return comment;
    }
    
    public List<Comment> getCommentByPage(Integer pageNum, Integer pageSize, Long blogId) {
        if (pageNum < 1 || pageSize < 1)
            return null;
        return commentMapper.getPagination((pageNum - 1) * pageSize, pageNum * pageSize, blogId);
    }
    
    @Override
    public void cleanCommentCache(String blogId) {
        if (blogId == null || blogId.length() == 0 || "-1".equals(blogId)) {
            //删除留言缓存
            redisUtil.deleteByPrefix(REPLY_KEY);
            redisUtil.delete(REPLY_COUNT_KEY);
        } else {
            //删除评论缓存
            redisUtil.deleteByPrefix(COMMENT_KEY + blogId);
            redisUtil.delete(COMMENT_COUNT_KEY + "-" + blogId);
        }
    }
}
