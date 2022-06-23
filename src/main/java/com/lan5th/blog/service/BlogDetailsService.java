package com.lan5th.blog.service;

import com.lan5th.blog.pojo.BlogDetail;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
public interface BlogDetailsService {
    /**
     * 获取单个博客详情
     * @param id
     * @return
     */
    BlogDetail getBlogDetail(long id);
    
    /**
     * 获取博客正文markdown
     * @param id
     * @return
     */
    String getContent(long id);
    
    /**
     * 创建博客(需要另外上传markdown文件)
     * @param blogDetail
     * @return
     */
    String newBlog(BlogDetail blogDetail);
    
    /**
     * 更新博客(需要另外更新markdown文件)
     * @param blogDetail
     * @return
     */
    String updateBlog(BlogDetail blogDetail);
    
    /**
     * 删除博客
     * @param id
     * @return
     */
    String deleteBlog(long id);
}
