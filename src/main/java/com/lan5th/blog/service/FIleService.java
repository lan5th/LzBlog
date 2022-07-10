package com.lan5th.blog.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;

/**
 * @author lan5th
 * @date 2022/6/29 16:35
 */
public interface FIleService {
    /**
     * 上传文件，返回的是真实的markdown文件保存位置
     * @param file
     * @param blogId
     * @return
     * @throws IOException
     */
    String upload(MultipartFile file, String blogId) throws IOException;
    
    /**
     * 删除博客对应的文件
     * @param location
     */
    void delete(String location);
    
    /**
     * 获取博客正文markdown
     * @param id
     * @return
     */
    String getContent(String id);
}
