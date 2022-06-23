package com.lan5th.blog.pojo;

import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.sql.Date;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Data
public class User {
    private Long id;
    private String name;
    private Long qqId;
    private String avatarUrl;
    private Boolean deleted;
    private Boolean isAdmin;
    private Date createTime;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
