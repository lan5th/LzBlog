package com.lan5th.blog.service;

import com.lan5th.blog.pojo.BlogDetail;

public interface BlogDetailsService {
    BlogDetail getBlogDetail(long id);
    String getContent(long id);
    String newBlog(BlogDetail blogDetail);
    String updateBlog(BlogDetail blogDetail);
    String deleteBlog(long id);
}
