package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.BlogMapper;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;
import com.lan5th.blog.service.BlogDetailsService;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.service.FIleService;
import com.lan5th.blog.service.TagsService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Service
public class BlogDetailsServiceImpl implements BlogDetailsService {
    //前缀标识
    private static final String CACHE_KEY = "detail-";
    //过期时间
    private static final int EXPIRE_TIME = 5;
    @Autowired
    private BlogMapper mapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FIleService fIleService;
    @Autowired
    private BlogsService blogsService;
    @Autowired
    private TagsService tagsService;

    /**
     * 尝试查询缓存
     * @param id
     * @return
     */
    @Override
    public BlogDetail getBlogDetail(String id) {
        BlogDetail blogDetail = null;
        long blogId = Long.parseLong(id);
        Object o = redisUtil.get(CACHE_KEY + blogId);
        if (o  == null) {
            blogDetail = mapper.getById(blogId);
            if (blogDetail != null) {
                redisUtil.set(CACHE_KEY + blogId, blogDetail, EXPIRE_TIME);
            }
        } else {
            blogDetail = (BlogDetail) o;
        }
        return blogDetail;
    }
    
    @Override
    public String getContent(String id) {
        return fIleService.getContent(id);
    }
    
    /**
     * 文件保存由FileController实现，Service层仅封装持久化逻辑
     * @param blogDetail
     */
    @Override
    public String create(BlogDetail blogDetail) {
        if (blogDetail == null) {
            return null;
        }
        
        //数据库必需字段
        blogDetail.setIdIfNew();
        if (blogDetail.getBlogName() == null)
            blogDetail.setBlogName("");
        if (blogDetail.getTagId() == null)
            blogDetail.setTagId(0L);
        
        if (redisUtil.hasKey(CACHE_KEY + blogDetail.getId()) || mapper.getById(blogDetail.getId()) != null)
            return "博客已存在，请勿重复上传";
        mapper.save(blogDetail);
        redisUtil.delete(CACHE_KEY + blogDetail.getId());
        blogsService.cleanListCache();
        tagsService.cleanTagsCache();
        return "博客上传成功";
    }
    
    /**
     * 同上，只有持久化逻辑
     * @param blogDetail
     * @return
     */
    @Override
    public String updateBlog(BlogDetail blogDetail) {
        BlogDetail oldBlog = mapper.getById(blogDetail.getId(), true);
        if (oldBlog == null)
            return "博客不存在，请重新上传";
        //更新博客时如果旧博客路径存在则需要删除旧文件
        if (blogDetail.getLocation() != null && oldBlog.getLocation() != null &&
                !blogDetail.getLocation().equals(oldBlog.getLocation())) {
            fIleService.delete(oldBlog.getLocation());
        }
        mapper.update(blogDetail);
        redisUtil.delete(CACHE_KEY + blogDetail.getId());
        return "博客更新成功";
    }

    @Override
    public String deleteBlog(String id) {
        long blogId = Long.parseLong(id);
        if (mapper.getById(blogId) == null)
            return "博客不存在，请重新上传";
        //必须先删除文件，再删除数据
        BlogDetail detail = getBlogDetail(id);
        fIleService.delete(detail.getLocation());
    
        //删除博客数据
        ArrayList<Long> idList = new ArrayList<>();
        idList.add(Long.parseLong(id));
        mapper.deleteByIds(idList);
        
        //需要判断对应tag是否已经为null
        String tagId = String.valueOf(detail.getTagId());
        Tag tag = tagsService.getTag(tagId);
        //当tag下面没有博客时TagMapper会查不出数据，因此这里会是null
        if (tag == null || tag.getBlogCount() == 0) {
            tagsService.delTag(tagId);
        }
    
        //清除相关缓存
        redisUtil.delete(CACHE_KEY + blogId);
        blogsService.cleanListCache();
        tagsService.cleanTagsCache();
        return null;
    }
}
