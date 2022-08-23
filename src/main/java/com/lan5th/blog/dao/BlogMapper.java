package com.lan5th.blog.dao;

import com.lan5th.blog.pojo.BlogDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 * 其实只需要一个@Mapper就足够了，添加@Component是解决idea报错
 */
@Mapper
@Component
public interface BlogMapper {
    BlogDetail getById(Long id);
    
    BlogDetail getById(Long id, Boolean ignoreDelete);

    void update(BlogDetail blogDetail);

    void save(BlogDetail blogDetail);

    void deleteByIds(List<Long> ids);

    void physicalRemoveByIds(List<Long> id);
    
    List<BlogDetail> getPagination(Integer preNum, Integer postNum, Long tagId);
    
    List<BlogDetail> getByIds(List<Long> ids);
    
    int getTotalCount();
    
    void batchUpdateViews(@Param("viewsMap") HashMap<Long, Integer> viewsMap);
//    void batchUpdateViews(@Param("viewsMap")Set<Long, Integer> viewsMap);
}
