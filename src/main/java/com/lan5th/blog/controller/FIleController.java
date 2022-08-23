package com.lan5th.blog.controller;

import com.lan5th.blog.anotation.RequireToken;
import com.lan5th.blog.pojo.BlogDetail;
import com.lan5th.blog.service.BlogDetailsService;
import com.lan5th.blog.service.FIleService;
import com.lan5th.blog.utils.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * 博客正文文件操作
 * @author lan5th
 * @date 2022/6/29 16:02
 */
@Controller
@RequestMapping("/file")
public class FIleController {
    private static final Long FILE_SIZE_LIMIT = 1L * 1024L * 1024L;
    @Resource
    private BlogDetailsService blogDetailsService;
    @Resource
    private FIleService fIleService;
    
    /**
     * 数据和文件必须异步上传
     * 上传文件早于保存数据
     * 需要手动新建一个BlogDetail实体类并自行获取id
     * @param file
     * @param params
     * @return
     * @throws IOException
     */
    @Transactional
    @RequireToken
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject upload(@RequestParam("file") MultipartFile file, @RequestParam Map<String, Object> params) throws IOException {
        JsonObject res = new JsonObject();
        if (!verifyFile(file, res)) {
            //验证不通过
            return res;
        }
        
        String blogId = (String)params.get("blogId");
        //layui限制，必须作"null"值的限制
        if (blogId == null || blogId.length() == 0 || "null".equals(blogId)) {
            BlogDetail detail = new BlogDetail();
            blogDetailsService.create(detail);
            blogId = detail.getId().toString();
        }
        String uploadPath = fIleService.upload(file, blogId);
    
        //更新blog_detail表中的location数据
        BlogDetail detail = new BlogDetail();
        detail.setId(Long.parseLong(blogId));
        detail.setLocation(uploadPath);
        detail.setDeleted(true);
        blogDetailsService.updateBlog(detail);
        res.put("blogId", detail.getId());
        res.put("blogName", file.getOriginalFilename().split("\\.")[0]);
        return res;
    }
    
    /**
     * 验证是否通过
     * @param res 这里如果检验不通过会在res中设置status和message
     * @return 检验结果
     */
    private Boolean verifyFile(MultipartFile file, JsonObject res) {
        if (file.getSize() > FILE_SIZE_LIMIT) {
            res.setStatus(false);
            res.setMessage("文件不能超过最大值1M!");
            return false;
        }
        String originName = file.getOriginalFilename();
        String[] names = originName.split("\\.");
        String lastName = names[names.length - 1];
        if (!"md".equals(lastName) && !"markdown".equals(lastName)) {
            res.setStatus(false);
            res.setMessage("文件类型不正确！请上传markdown文件");
            return false;
        }
        return true;
    }
}
