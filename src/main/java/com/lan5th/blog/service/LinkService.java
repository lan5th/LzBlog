package com.lan5th.blog.service;

import com.lan5th.blog.pojo.Link;

import java.util.List;

/**
 * @author lan5th
 * @date 2022/7/26 13:29
 */
public interface LinkService {
    List<Link> getAllRecommendLinks();
    
    List<Link> getAllFriendLinks();
    
    void saveLink(Link link);
    
    void deleteLink(String id);
}
