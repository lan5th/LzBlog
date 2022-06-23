package com.lan5th.blog.pojo;

import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private Long id;
    //blogId为0是表示留言，不为0时表示博客评论
    private Long blogId;
    private String content;
    private Date createTime;
    private Boolean deleted;
    private Long userId;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
    
    public boolean isDeleted() {
        return deleted;
    }
    
    public boolean isBlogComment() {
        return this.blogId != 0L;
    }
}
