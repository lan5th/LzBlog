package com.lan5th.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Data
public class BlogDetail implements Serializable {
    private static final Long serializeVersion = 1L;
    
    private Long id;
    private String blogName;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;
    private Boolean deleted;
    private String tagName;
    private Long tagId;
    private String shortContent;
    private Integer views;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
}
