package com.lan5th.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.util.Date;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Data
public class Comment implements Comparable<Comment>{
    private Long id;
    //blogId为-1是表示留言，不为0时表示博客评论
    private Long blogId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private Boolean deleted;
    private Long userId; //评论者id
    private String userName;
    private String userAvatar;
    private Long replyTo; //被回复者id
    private String replyToUserName;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
    
    public boolean isDeleted() {
        return deleted;
    }
    
    @JsonIgnore
    public boolean isBlogComment() {
        return this.blogId != 0L && this.blogId != -1L;
    }
    
    //按发表时间进行排序
    @Override
    public int compareTo(Comment o) {
        return this.createTime.compareTo(o.getCreateTime()) ;
    }
}
