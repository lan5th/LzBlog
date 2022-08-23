package com.lan5th.blog.pojo;

import java.io.Serializable;
import java.util.Date;

import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

/**
 * 友情链接
 * @TableName links
 */
@Data
public class Link implements Serializable {
    private static final Long serializeVersion = 1L;
    
    private Long id;
    
    private String linkName;

    private String url;
    
    private Date createTime;
    
    private Boolean enabled;

    /**
     * 推荐文章：1
     * 友情链接：2
     */
    private Integer type;
    
    public void setIdIfNew() {
        if (this.id == null) {
            this.id = UIDUtil.getNewId();
        }
    }
}
