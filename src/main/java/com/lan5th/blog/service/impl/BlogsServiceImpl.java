package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.BlogMapper;
import com.lan5th.blog.dao.TagMapper;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Service
public class BlogsServiceImpl implements BlogsService {
    //缓存标识
    private static final String CACHE_KEY = "index-";
    private static final String TOP_KEY = "top";
    private static final String TOTAL_KEY = "totalCount";
    //过期时间
    private static final int EXPIRE_TIME = 5;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public List<BlogDetail> getIndexBlogs(int pageNum, int pageSize) {
        if (pageNum < 1 || pageSize < 1)
            return null;
        List<BlogDetail> list = null;
        Object o = redisUtil.get(CACHE_KEY + pageNum + "-" + pageSize);
        if (o == null) {
            list = instantGetBlogsByPage(pageNum, pageSize, null);
            if (list != null) {
                redisUtil.set(CACHE_KEY + pageNum + "-" + pageSize, list, EXPIRE_TIME);
            }
        } else {
            list = (List<BlogDetail>) o;
        }
        return list;
    }
    
    @Override
    public List<BlogDetail> getTopBlogs() {
        Object o = redisUtil.get(TOP_KEY);
        if (o != null) {
            return (List<BlogDetail>) o;
        }
        return null;
    }
    
    @Override
    public void newTopBlog(String blogId) {
        if (blogId == null)
            return;
        List<BlogDetail> oldList = (List<BlogDetail>)redisUtil.get(TOP_KEY);
        ArrayList<Long> idList = new ArrayList<>();
        //默认新博客排在最后面
        if (oldList != null) {
            for (BlogDetail detail : oldList) {
                idList.add(detail.getId());
            }
        }
        idList.add(Long.parseLong(blogId));
        setTopBlog(idList);
    }
    
    @Override
    public void cancelTop(String blogId) {
        if (blogId == null)
            return;
        List<BlogDetail> oldList = (List<BlogDetail>)redisUtil.get(TOP_KEY);
        if (oldList == null)
            return;
        long id = Long.parseLong(blogId);
        ArrayList<BlogDetail> newList = new ArrayList<>();
        for (BlogDetail detail : oldList) {
            if (detail.getId().equals(id))
                continue;
            newList.add(detail);
        }
        redisUtil.set(TOP_KEY, newList, -1);
    }
    
    @Override
    public void removeAllTops() {
        setTopBlog(null);
    }
    
    //置顶队列只保存在redis中，因此只能set的时候进行保存，get的时候无法保存redis
    private void setTopBlog(List<Long> idList) {
        if (idList == null || idList.size() == 0) {
            redisUtil.delete(TOP_KEY);
        }
        //这里排序无法依据指定顺序进行排序，暂时搁置
        List<BlogDetail> tops = blogMapper.getByIds(idList);
        //-1表示无过期时间
        redisUtil.set(TOP_KEY, tops, -1);
    }
    
    @Override
    public Integer getTotalCount() {
        Integer totalCount = 0;
        if ((totalCount = (Integer) redisUtil.get(TOTAL_KEY)) == null) {
            totalCount = blogMapper.getTotalCount();
            redisUtil.set(TOTAL_KEY, totalCount, EXPIRE_TIME);
        }
        return totalCount;
    }
    @Override
    public List<BlogDetail> instantGetBlogsByPage(Integer pageNum, Integer pageSize, Long tagId) {
        if (pageNum < 1 || pageSize < 1)
            return null;
        return blogMapper.getPagination((pageNum - 1) * pageSize, pageNum * pageSize, tagId);
    }
    
    @Override
    public void cleanListCache() {
        redisUtil.deleteByPrefix(CACHE_KEY);
    }
}
