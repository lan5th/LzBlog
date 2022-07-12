package com.lan5th.blog.service.impl;

import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.service.BlogDetailsService;
import com.lan5th.blog.service.FIleService;
import com.lan5th.blog.utils.RedisUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * @author lan5th
 * @date 2022/6/29 16:38
 */
@Service
public class FileServiceImpl implements FIleService, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    private static String FILE_PATH;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        //获取jar包物理路径
        File jarFile = null;
        String filePath = null;
        try {
            ApplicationHome ah = new ApplicationHome(getClass());
            jarFile = ah.getSource();
            filePath = jarFile.getParentFile().getPath() + "/upload/";
            FILE_PATH = filePath;
            logger.info("初始化文件上传路径:" + filePath);
        } catch (NullPointerException e) {
            // TODO 运行单元测试时ApplicationHome(getClass())会报空指针异常
        }
    }
    
    private static final String CONTENT_KEY = "blogContent-";
    //1min缓存
    private static final Integer EXPIRE_TIME = 60;
    @Autowired
    private BlogDetailsService blogDetailsService;
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public String upload(MultipartFile file, String blogId) throws IOException {
        Date date = new Date();
        int year = date.getYear();
        int month = date.getMonth();
        //年月子文件夹路径前缀
        String datePath = "/" + (1900 + year) + "-" + (1 + month);
        File realPath = new File(FILE_PATH + "public/posts" + datePath);
        if (!realPath.exists()) {
            realPath.mkdir();
        }
        String blogPath = realPath + "/" + blogId;
        //保存文件
        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(blogPath));
        logger.info("blogId:" + blogId + ",上传文件路径：" + blogPath);
        return "public/posts" + datePath + "/" + blogId;
    }
    
    @Override
    public void delete(String location) {
        if (location == null)
            return;
        File toBeDelete = new File(FILE_PATH + location);
        if (!toBeDelete.exists()) {
            logger.error("文件不存在,文件路径:" + location);
        } else {
            toBeDelete.delete();
        }
    }
    
    @Override
    public String getContent(String id) {
        String content;
        if ((content = (String)redisUtil.get(CONTENT_KEY + id)) == null) {
            StringBuilder builder = new StringBuilder();
            BlogDetail detail = blogDetailsService.getBlogDetail(id);
            String location = detail.getLocation();
            if (location == null || location.length() == 0)
                return null;
            location = FILE_PATH + location;
            File contentFile = null;
            
            try {
                contentFile = ResourceUtils.getFile(location);
            } catch (FileNotFoundException e) {
                logger.error("文件不存在，博客id:" + id);;
            }
            
            //流操作使用try-with-resource方式，更安全
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(contentFile))) {
                String tmpLine;
                int i = 2;
                while ((tmpLine = bufferedReader.readLine()) != null) {
                    if (i > 0) {
                        if (tmpLine.equals("---"))
                            i--;
                    } else if (!tmpLine.equals("<!--more-->")) {
                        builder.append(tmpLine);
                        builder.append("\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            content = builder.toString();
            redisUtil.set(CONTENT_KEY + id, content, EXPIRE_TIME);
        }
        return content;
    }
}
