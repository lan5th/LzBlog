package com.lan5th.blog.service;

import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.pojo.Tag;

import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
public interface TagsService {
    /**
     * 获取所有的tag
     * @return
     */
    List<Tag> getAllTags();
    
    /**
     * 获取tag下的博客列表
     * @param pageNum
     * @param pageSize
     * @param tagId
     * @return
     */
    List<BlogDetail> getBlogList4Tag(Integer pageNum, Integer pageSize, Long tagId);
    
    /**
     * 获取所有tag数量(首页用)
     * @return
     */
    int getAllTagCount();
    
    void saveTag(Tag tag);
    
    /**
     * @param tagId
     * @param tagName
     * @return 返回值为null时删除成功，返回值不为null则是报错提示信息
     */
    String updateTag(String tagId, String tagName);
    
    /**
     * 为保证博客数据安全，请先确认tag下没有博客之后再删除
     * 这里只有删除逻辑，不再保留校验逻辑
     * @param tagId
     * @return 返回值为null时删除成功，返回值不为null则是报错提示信息
     */
    String delTag(String tagId);
    
    void cleanTagsCache();
    
    Tag getTag(String id);
}
