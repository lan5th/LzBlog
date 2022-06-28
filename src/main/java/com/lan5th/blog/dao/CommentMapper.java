package com.lan5th.blog.dao;

import com.lan5th.blog.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Mapper
@Component
public interface CommentMapper {
    void update(Comment comment);
    
    void save(Comment comment);
    
    void deleteByIds(List<Long> ids);
    
    /**
     * 分页查询comment
     * blogId:
     *      不为-1:   查询某个博客的评论
     *      为-1:    查询所有留言
     *      为null:  查询所有博客评论(暂不使用)
     * @param preNum
     * @param postNum
     * @return
     */
    List<Comment> getPagination(Integer preNum, Integer postNum, Long blogId);
    
    /**
     * 获取所有comment计数
     * blogId:
     *      不为-1:   查询某个博客的评论计数
     *      为-1:    查询所有留言计数
     *      为null:  查询所有博客评论计数
     * @return
     */
    Integer getCommentCount(Long blogId);
}
