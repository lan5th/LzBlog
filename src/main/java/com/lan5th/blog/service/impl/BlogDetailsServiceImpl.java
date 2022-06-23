package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.BlogMapper;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.service.BlogDetailsService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Service
public class BlogDetailsServiceImpl implements BlogDetailsService {
    //前缀标识
    private static final String CACHE_KEY = "detail-";
    //过期时间
    private static final int EXPIRE_TIME = 5;
    @Autowired
    private BlogMapper mapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 尝试查询缓存
     * @param id
     * @return
     */
    @Override
    public BlogDetail getBlogDetail(long id) {
        BlogDetail blogDetail = null;
        Object o = redisUtil.get(CACHE_KEY + id);
        if (o  == null) {
            blogDetail = mapper.getById(id);
            if (blogDetail != null) {
                redisUtil.set(CACHE_KEY + id, blogDetail, EXPIRE_TIME);
            }
        } else {
            blogDetail = (BlogDetail) o;
        }
        return blogDetail;
    }

    /**
     * 根据location获取文章内容
     * @param id
     * @return
     */
    @Override
    public String getContent(long id) {
        BlogDetail details = getBlogDetail(id);

        if (details == null)
            return "博客不存在";
        else if (details.isDeleted())
            return "博客已被删除，请联系管理员";

        StringBuilder builder = new StringBuilder();
        try {
            File file = ResourceUtils.getFile("classpath:public/posts/" + details.getId());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String tmpLine;
            int i = 2;
            while ((tmpLine = bufferedReader.readLine()) != null) {
                if (i > 0) {
                    if (tmpLine.equals("---"))
                        i--;
                } else {
                    builder.append(tmpLine);
                    builder.append("\n");
                }
            }

            fileReader.close();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * 文件保存由Controller实现，Service层仅封装持久化逻辑
     * @param blogDetail
     */
    @Override
    public String newBlog(BlogDetail blogDetail) {
        if (redisUtil.hasKey(CACHE_KEY + blogDetail.getId()) || mapper.getById(blogDetail.getId()) != null)
            return "博客已存在，请勿重复上传";
        mapper.save(blogDetail);
        redisUtil.delete(CACHE_KEY + blogDetail.getId());
        return "博客上传成功";
    }

    @Override
    public String updateBlog(BlogDetail blogDetail) {
        if (mapper.getById(blogDetail.getId()) == null)
            return "博客不存在，请重新上传";
        mapper.update(blogDetail);
        redisUtil.delete(CACHE_KEY + blogDetail.getId());
        return "博客更新成功";
    }

    @Override
    public String deleteBlog(long id) {
        // TODO 文件删除部分
        if (mapper.getById(id) == null)
            return "博客不存在，请重新上传";
        redisUtil.delete(CACHE_KEY + id);
        // TODO ids和id转接适配
        mapper.deleteByIds(new ArrayList<>());
        return "博客删除成功";
    }
}
