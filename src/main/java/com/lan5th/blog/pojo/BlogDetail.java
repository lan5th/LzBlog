package com.lan5th.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.util.Date;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Data
public class BlogDetail {
    private Long id;
    private String blogName;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    private Boolean deleted;
    private String tagName;
    private Long tagId;
    private String shortContent;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
}
