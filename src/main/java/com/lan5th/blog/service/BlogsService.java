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
     * 设定置顶博客顺序
     * 传递null进来则为删除全部置顶博客
     * @param topList
     */
    void setTopBlog(List<Long> topList);
    
    Integer getTotalCount();
    
    /**
     * tagId传null即为全部查询
     * @param pageNum
     * @param pageSize
     * @param tagId
     * @return
     */
    List<BlogDetail> instantGetBlogsByPage(Integer pageNum, Integer pageSize, String tagId);
}
