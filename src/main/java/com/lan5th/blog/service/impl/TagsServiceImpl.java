package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.TagMapper;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.service.TagsService;
import com.lan5th.blog.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Service
public class TagsServiceImpl implements TagsService {
    private static final Logger logger = LoggerFactory.getLogger(TagsServiceImpl.class);
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
    
    @Override
    public void saveTag(Tag tag) {
        if (tag != null) {
            tagMapper.save(tag);
        }
    }
    
    @Override
    public String updateTag(String tagId, String tagName) {
        Tag byId = tagMapper.getById(Long.parseLong(tagId));
        if (byId == null) {
            return "所选择的tag已被删除或不存在";
        }
        byId.setTagName(tagName);
        tagMapper.update(byId);
        return null;
    }
    
    @Override
    public String delTag(String tagId) {
        ArrayList<Long> idList = new ArrayList<>();
        idList.add(Long.parseLong(tagId));
        tagMapper.deleteByIds(idList);
        return null;
    }
    
    @Override
    public void cleanTagsCache() {
        redisUtil.deleteByPrefix(TAGS_KEY);
    }
    
    @Override
    public Tag getTag(String id) {
        if (id == null || id.length() == 0) {
            return null;
        }
        return tagMapper.getById(Long.parseLong(id));
    }
}
