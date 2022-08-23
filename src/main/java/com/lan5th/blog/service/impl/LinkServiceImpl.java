package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.LinksMapper;
import com.lan5th.blog.pojo.Link;
import com.lan5th.blog.service.LinkService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lan5th
 * @date 2022/7/26 14:03
 */
@Service
public class LinkServiceImpl implements LinkService {
    private static final String LINK_KEY = "link-";
    private static final Integer EXPIRE_TIME = 30;
    @Resource
    private LinksMapper linksMapper;
    @Resource
    private RedisUtil redisUtil;
    
    @Override
    public List<Link> getAllRecommendLinks() {
        List<Link> list = (List) redisUtil.get(LINK_KEY + "recommend");
        if (list == null) {
            list = linksMapper.getLinkByType(1);
            redisUtil.set(LINK_KEY + "recommend", list, EXPIRE_TIME);
        }
        return list;
    }
    
    @Override
    public List<Link> getAllFriendLinks() {
        List<Link> list = (List) redisUtil.get(LINK_KEY + "friend");
        if (list == null) {
            list = linksMapper.getLinkByType(2);
            redisUtil.set(LINK_KEY + "friend", list, EXPIRE_TIME);
        }
        return list;
    }
    
    @Override
    public void saveLink(Link link) {
        link.setIdIfNew();
        linksMapper.save(link);
        if (link.getType() == 1)
            redisUtil.delete(LINK_KEY + "recommend");
        else
            redisUtil.delete(LINK_KEY + "friend");
    }
    
    @Override
    public void deleteLink(String idStr) {
        long id = Long.parseLong(idStr);
        Link link = linksMapper.getById(id);
        if (link == null) {
            throw new RuntimeException("id在数据库中没有对应记录");
        }
        linksMapper.deleteById(id);
        if (link.getType() == 1)
            redisUtil.delete(LINK_KEY + "recommend");
        else
            redisUtil.delete(LINK_KEY + "friend");
    }
}
