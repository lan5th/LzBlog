package com.lan5th.blog.service;


import com.lan5th.blog.pojo.BlogDetail;

import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
public interface BlogsService {
    /**
     * 按时间分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<BlogDetail> getIndexBlogs(int pageNum, int pageSize);
    
    /**
     * 获取置顶博客信息
     * @return
     */
    List<BlogDetail> getTopBlogs();
    
    /**
     * 新建博客置顶
     * 传递null进来则为删除全部置顶博客
     * @param blogId 新置顶的博客id
     */
    void newTopBlog(String blogId);
    
    void cancelTop(String blogId);
    
    void removeAllTops();
    
    int getTotalCount();
    
    /**
     * tagId传null即为全部查询
     * @param pageNum
     * @param pageSize
     * @param tagId
     * @return
     */
    List<BlogDetail> instantGetBlogsByPage(Integer pageNum, Integer pageSize, Long tagId);
    
    void cleanListCache();
}
