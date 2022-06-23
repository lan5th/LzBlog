package com.lan5th.blog.service;

import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;

import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
public interface TagsService {
    List<Tag> getAllTags();
    
    List<BlogDetail> getBlogList4Tag(Integer pageNum, Integer pageSize, String tagId);
}
