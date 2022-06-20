package com.lan5th.blog.service.impl;

import com.lan5th.blog.dao.BlogDetailsMapper;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.service.BlogDetailsService;
import com.lan5th.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Service
public class BlogDetailsServiceImpl implements BlogDetailsService {
    @Autowired
    private BlogDetailsMapper mapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 尝试查询缓存
     * @param id
     * @return
     */
    @Override
    public BlogDetail getBlogDetail(long id) {
        BlogDetail blogDetail;
        if ((blogDetail = (BlogDetail) redisUtil.get(String.valueOf(id))) == null) {
            blogDetail = mapper.getById(id);
            redisUtil.set(String.valueOf(id), blogDetail, 5);
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
            File file = ResourceUtils.getFile("classpath:posts/" + details.getLocation());
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
        if (redisUtil.hasKey(String.valueOf(blogDetail.getBlogId())) || mapper.getById(blogDetail.getBlogId()) != null)
            return "博客已存在，请勿重复上传";
        mapper.insert(blogDetail);
        return "博客上传成功";
    }

    @Override
    public String updateBlog(BlogDetail blogDetail) {
        if (mapper.getById(blogDetail.getBlogId()) == null)
            return "博客不存在，请重新上传";
        mapper.update(blogDetail);
        return "博客更新成功";
    }

    @Override
    public String deleteBlog(long id) {
        if (mapper.getById(id) == null)
            return "博客不存在，请重新上传";
        if (redisUtil.hasKey(String.valueOf(id)))
            redisUtil.delete(String.valueOf(id));
        mapper.deleteById(id);
        return "博客删除成功";
    }
}
