package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.BlogMapper;
import com.lan5th.blog.dao.TagMapper;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.service.BlogsService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private static final int EXPIRE_TIME = 10;
    @Autowired
    BlogMapper blogMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    TagMapper tagMapper;
    
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
    
    //置顶队列只保存在redis中，因此只能set的时候进行保存，get的时候无法保存redis
    @Override
    public void setTopBlog(List<Long> idList) {
        if (idList == null || idList.size() == 0) {
            redisUtil.delete(TOP_KEY);
        }
        List<BlogDetail> tops = blogMapper.getByIds(idList);
//        for (BlogDetail top : tops) {
//            top.setCreateTime(top.getCreateTime());
//        }
        //-1表示无过期时间
        redisUtil.set(TOP_KEY, tops, -1);
    }
    
    
    public Integer getTotalCount() {
        Integer totalCount = 0;
        if ((totalCount = (Integer) redisUtil.get(TOTAL_KEY)) == null) {
            totalCount = blogMapper.getTotalCount();
            redisUtil.set(TOTAL_KEY, totalCount, EXPIRE_TIME);
        }
        return totalCount;
    }
    
    public List<BlogDetail> instantGetBlogsByPage(Integer pageNum, Integer pageSize, String tagId) {
        if (pageNum < 1 || pageSize < 1)
            return null;
        return blogMapper.getPagination((pageNum - 1) * pageSize, pageNum * pageSize, tagId);
    }
}
