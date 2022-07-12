package com.lan5th.blog.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String openId;
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
    
    //不加这个注解在序列化的时候会报错
    @JsonIgnore
    public boolean isAdmin() {return isAdmin;}
}
