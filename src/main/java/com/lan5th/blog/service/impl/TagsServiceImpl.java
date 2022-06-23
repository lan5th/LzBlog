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

@Service
public class TagsServiceImpl implements TagsService {
    private static final String TAGS_KEY = "tags";
    private static final int EXPIRE_TIME = 10;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    TagMapper tagMapper;
    @Autowired
    BlogsService blogsService;
    
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
    public List<BlogDetail> getBlogList4Tag(Integer pageNum, Integer pageSize, String tagId) {
        List<BlogDetail> list = null;
        if ((list = (List<BlogDetail>) redisUtil.get(TAGS_KEY + "-" + tagId + "-" + pageNum + "-" + pageSize)) == null) {
            list = blogsService.instantGetBlogsByPage(pageNum, pageSize, tagId);
            redisUtil.set(TAGS_KEY + "-" + tagId + "-" + pageNum + "-" + pageSize, list, 10);
        }
        return list;
    }
}
