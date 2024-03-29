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
    BlogDetail getBlogDetail(String id);
    
    /**
     * 获取博客正文markdown
     * @param id
     * @return
     */
    String getContent(String id);
    
    /**
     * 创建博客(需要另外上传markdown文件)
     * @param blogDetail
     * @return
     */
    String create(BlogDetail blogDetail);
    
    /**
     * 更新博客(需要另外更新markdown文件)
     * @param blogDetail
     * @return
     */
    String updateBlog(BlogDetail blogDetail);
    
    /**
     * 删除博客
     * @param id
     * @return 为null则删除成功
     */
    String deleteBlog(String id);
    
    /**
     * 增加阅读量
     * @return
     */
    boolean increaseView(String blogId, String ipHost);
}
