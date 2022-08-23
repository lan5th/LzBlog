package com.lan5th.blog.pojo;

import com.lan5th.blog.utils.UIDUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Tag没有逻辑删除，只有物理删除
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Data
public class Tag implements Serializable {
    private static final Long serializeVersion = 1L;
    
    private Long id;
    private String tagName;
    private Date createTime;
    private Integer blogCount;
    
    public void setIdIfNew() {
        if (this.id == null)
            this.id = UIDUtil.getNewId();
    }
}
