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
     * 分页，blogId来区分是评论还是留言
     * @param preNum
     * @param postNum
     * @return
     */
    List<Comment> getPagination(Integer preNum, Integer postNum, Long blogId);
}
