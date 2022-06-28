package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.TagMapper;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.service.TagsService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Service
public class TagsServiceImpl implements TagsService {
    private static final String TAGS_KEY = "tags";
    private static final int EXPIRE_TIME = 10;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private BlogsService blogsService;
    
    @Override
    public List<Tag> getAllTags() {
        List<Tag> list = null;
        if ((list = (List<Tag>) redisUtil.get(TAGS_KEY)) == null) {
            list = tagMapper.getAllTags();
            redisUtil.set(TAGS_KEY, list, EXPIRE_TIME);
        }
        return list;
    }
    
    @Override
    public List<BlogDetail> getBlogList4Tag(Integer pageNum, Integer pageSize, Long tagId) {
        List<BlogDetail> list = null;
        if ((list = (List<BlogDetail>) redisUtil.get(TAGS_KEY + "-" + tagId + "-" + pageNum + "-" + pageSize)) == null) {
            list = blogsService.instantGetBlogsByPage(pageNum, pageSize, tagId);
            redisUtil.set(TAGS_KEY + "-" + tagId + "-" + pageNum + "-" + pageSize, list, 10);
        }
        return list;
    }
    
    @Override
    public Integer getAllTagCount() {
        return getAllTags().size();
    }
}
