package com.lan5th.blog.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 */
@Component
public class RedisUtil {
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    
    /**
     * 根据前缀删除key
     * @param prefix 要删除的前缀key
     */
    @SuppressWarnings("unchecked")
    public void deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }
    
    public boolean exist(String key) {
        return get(key) != null;
    }

    /**
     * 普通缓存放入
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    private boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间（秒单位单独方法）
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */

    public boolean set(String key, Object value, long time) {
        return set(key, value, time, TimeUnit.SECONDS);
    }
    
    /**
     * 普通缓存放入并设置时间
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                if (timeUnit != null) {
                    redisTemplate.opsForValue().set(key, value, time, timeUnit);
                } else {
                    redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    
                }
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
//    /**
//     * 过期时间问题，已弃用
//     */
//    public boolean getSetAndSave(String setName, String value) {
//        Boolean exists = false;
//        try {
//            exists = redisTemplate.opsForSet().isMember(setName, value);
//            if (!exists) {
//                redisTemplate.opsForSet().add(setName, value, 1);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return exists;
//    }
    
    public boolean existsAndSet(String key, long time) {
        boolean noExists = true;
        try {
            noExists = redisTemplate.opsForValue().setIfAbsent(key, null, time, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !noExists;
    }
    
    /**
     * 自增的hash类型操作
     * @param hashName 对应hashMap的key
     * @param key 对应value的key
     * @return
     */
    public boolean incHashKey(String hashName, String key) {
        try {
            Integer oldVal = (Integer) redisTemplate.opsForHash().get(hashName, key);
            //自增
            if (oldVal == null) {
                redisTemplate.opsForHash().put(hashName, key, 1);
            } else {
                redisTemplate.opsForHash().increment(hashName, key, 1);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取一个hash的所有值
     * @param hashName 对应hash的key
     * @return
     */
    public Map<String, Object> getHash(String hashName) {
        Map<String, Object> map = null;
        try {
            map = redisTemplate.opsForHash().entries(hashName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    
    /**
     * 获取一个hash的所有值
     * @param hashName 对应hash的key
     * @return
     */
    public Object getHash(String hashName, String key) {
        Object val = null;
        try {
            val = redisTemplate.opsForHash().get(hashName, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }
}
