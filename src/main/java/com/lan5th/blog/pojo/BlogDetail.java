package com.lan5th.blog.pojo;

import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BlogDetail {
    private long blogId;
    private String blogName;
    private String location;
    private Date updateTime;
    private Date createTime;
    private boolean deleted;

    public BlogDetail(String blogName, String location) {
        this(UIDUtil.getNewId(), blogName, location);
    }

    public BlogDetail(long UID, String blogName, String location) {
        this.blogId = UID;
        this.blogName = blogName;
        this.location = location;
    }
}
