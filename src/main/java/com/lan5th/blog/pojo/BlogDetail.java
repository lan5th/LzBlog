package com.lan5th.blog.pojo;

import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.sql.Date;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Data
public class BlogDetail {
    private Long id;
    private String blogName;
    private String location;
    //updateTime在更新的时候需要手动赋值
    private Date updateTime;
    private Date createTime;
    private Boolean deleted;
    private String tagName;
    private Long tagId;
    private String shortContent;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
