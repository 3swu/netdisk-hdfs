package com.example.demo.Util;

import com.example.demo.Entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /*
    add string key
     */
    public void addKey(String key, Object value, long expire, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    /*
    set cache expire time
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0)
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    get value by key
     */
    public Object getValue(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    /*
    is key exists
     */
    public boolean hasKey(String key) {
        try {
            return this.redisTemplate.hasKey(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /*
    delete cache
     */
    public void del(String key) {
        this.redisTemplate.delete(key);
    }


}
