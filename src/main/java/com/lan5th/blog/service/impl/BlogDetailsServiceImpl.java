package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.BlogMapper;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;
import com.lan5th.blog.service.BlogDetailsService;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.service.FIleService;
import com.lan5th.blog.service.TagsService;
import com.lan5th.blog.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Slf4j
@Service
public class BlogDetailsServiceImpl implements BlogDetailsService {
    //前缀标识
    private static final String CACHE_KEY = "detail-";
    //防重复增加阅读量前缀标识,格式: blogView-blogId-ip
    private static final String BLOG_VIEW_IP_CHECK = "blogViewIp";
    //存放所有博客阅读量的hashKey
    private static final String ALL_BLOG_VIEW_COUNT = "allBlogViewCount";
    //过期时间
    private static final int EXPIRE_TIME = 5;
    @Resource
    private BlogMapper mapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private FIleService fIleService;
    @Resource
    private BlogsService blogsService;
    @Resource
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
        
        //获取缓存中的阅读量
        Integer views = (Integer) redisUtil.getHash(ALL_BLOG_VIEW_COUNT, id);
        if (views != null && views != 0) {
            blogDetail.setViews(views);
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
        
        if (redisUtil.hasKey(CACHE_KEY + blogDetail.getId()) || mapper.getById(blogDetail.getId()) != null) {
            log.warn("create-博客已存在,id:" +blogDetail.getId());
            return "博客已存在，请勿重复上传";
        }
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
            log.info("updateBlog-旧博客文件存在，需要删除旧博客文件:" + oldBlog.getLocation());
            fIleService.delete(oldBlog.getLocation());
        }
        mapper.update(blogDetail);
        redisUtil.delete(CACHE_KEY + blogDetail.getId());
        return "博客更新成功";
    }

    @Override
    public String deleteBlog(String id) {
        long blogId = Long.parseLong(id);
        if (mapper.getById(blogId) == null) {
            log.warn("deleteBlog-博客不存在,id:" + id);
            return "博客不存在，请重新上传";
        }
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
            log.info("该tag下所有博客已被清空，即将删除tag数据,tagId" + tagId);
            tagsService.delTag(tagId);
        }
    
        //清除相关缓存
        redisUtil.delete(CACHE_KEY + blogId);
        blogsService.cleanListCache();
        tagsService.cleanTagsCache();
        return null;
    }
    
    @Override
    public boolean increaseView(String blogId, String ipHost) {
        //ip检查30min过期
        boolean viewed = redisUtil.existsAndSet(BLOG_VIEW_IP_CHECK + "-" + blogId + "-" + ipHost, 30);
        //已经浏览过不增加浏览量
        if (!viewed) {
            redisUtil.incHashKey(ALL_BLOG_VIEW_COUNT, blogId);
            return true;
        }
        return false;
    }
    
    /**
     * 定时任务将浏览量存入数据库
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void save2Base(){
        log.info("执行保存浏览量定时任务");
        Map<String, Object> viewCount = redisUtil.getHash(ALL_BLOG_VIEW_COUNT);
        //将String:Object转换为Long:Integer
        HashMap<Long, Integer> viewCount1 = new HashMap<>();
        for (String k :viewCount.keySet()){
            Long nKey = Long.parseLong(k);
            Integer nVal = (Integer) viewCount.get(k);
            viewCount1.put(nKey, nVal);
        }
        mapper.batchUpdateViews(viewCount1);
        log.info("保存阅读数总量:" + viewCount1.size());
    }
}
