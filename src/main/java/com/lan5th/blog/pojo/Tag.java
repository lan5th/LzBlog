package com.lan5th.blog.pojo;

import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.util.Date;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Data
public class Tag {
    private Long id;
    private String tagName;
    private Date createTime;
    private Integer blogCount;
    private Boolean deleted;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
