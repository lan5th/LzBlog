package com.lan5th.blog.pojo;

import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Data
public class UserAuth implements Serializable {
    private static final Long serializeVersion = 1L;
    
    private Long id;
    private String password;
    private Boolean deleted;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}
