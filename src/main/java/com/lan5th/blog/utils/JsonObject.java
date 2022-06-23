package com.lan5th.blog.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
public class JsonObject implements Serializable {
    private static final Long serialVersionUID = 7574078101944305355L;
    private Boolean status;
    private String message;
    private Map<String, Object> data;
    public JsonObject() {
        this.data = new HashMap<>();
        this.status = true;
    }
    
    public void put(String key, Object data) {
        this.data.put(key, data);
    }
    
    public Object get(String key) {
        return data.get(key);
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }
    
    public boolean getStatus() {
        return this.status;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    /**
     * 只留给jackson使用
     * @return
     */
    public Map<String, Object> getData() {
        return this.data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
