package com.demo.light.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

//这是String的写入
    public void set(String key, String value,long timeout){
        try {
            if (key == null || value == null) {
                log.debug("key or value should not be null",key);
                return;
            }
            redisTemplate.opsForValue().set(key,value,timeout,TimeUnit.DAYS);
            log.debug("Redis SET key: {}", key);
        } catch (DataAccessException | SerializationException e) {
            log.error("Redis SET operation failed for key: {}", key, e);
        } catch (Exception e) {
            log.error("Unexpected error during Redis SET", e);
        }
    }


    /**
     * hash的操作
     */
    public void set(String key, String hkey, String value) {
        try {
            if (key == null || hkey == null) {
                log.debug(key,hkey);
                return;
            }
            redisTemplate.opsForHash().put(key,hkey, value);
            log.debug("Redis SET key: {}", key);
        } catch (DataAccessException | SerializationException e) {
            log.error("Redis SET operation failed for key: {}", key, e);
        } catch (Exception e) {
            log.error("Unexpected error during Redis SET", e);
        }
    }

//    public void set(String key, String hkey, Collection<? extends GrantedAuthority> authorities) {
//        try {
//            if (key == null || hkey == null) {
//                log.debug(key,hkey);
//                return;
//            }
//            redisTemplate.opsForHash().put(key,hkey, authorities);
//            log.debug("Redis SET key: {}", key);
//        } catch (DataAccessException | SerializationException e) {
//            log.error("Redis SET operation failed for key: {}", key, e);
//        } catch (Exception e) {
//            log.error("Unexpected error during Redis SET", e);
//        }
//    }




    /**
     * 获取指定 key 的值,hash
     */
    public String get(String key,String hkey) {

            try {
                return redisTemplate.opsForHash().get(key,hkey).toString();
            } catch (Exception e) {
                log.error("Redis GET operation failed for key: {}", key, e);
                return null;
            }

    }

//    public Collection<? extends GrantedAuthority> get(String key, String value) {
//
//        try {
//            return redisTemplate.opsForHash().get(key,value);
//        } catch (Exception e) {
//            log.error("Redis GET operation failed for key: {}", key, e);
//            return null;
//        }
//
//    }
    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis GET operation failed for key: {}", key, e);
            return null;
        }

    }

    public Boolean hasKey(String key,String hkey){
        try{
          return  redisTemplate.opsForHash().hasKey(key,hkey);
        }catch (Exception e){
            log.error(key,hkey,e);
        }
        return false;
    }

    /**
     * 删除指定 key
     */
    public boolean delete(String key,String hkey) {
        try {
            Long deleted = redisTemplate.opsForHash().delete(key,hkey);
            return deleted > 0L;
        } catch (Exception e) {
            log.error("Redis DELETE operation failed for key: {}", key,hkey, e);
            return false;
        }
    }
    public boolean delete(String key) {
        try {
            boolean deleted = redisTemplate.delete(key);
            return deleted;
        } catch (Exception e) {
            log.error("Redis DELETE operation failed for key: {}", key, e);
            return false;
        }
    }

//    public void add(String key , String value, long expiration){
//        redisTemplate.opsForZSet().add(key,value,(double) expiration);
//    }
//    public double score(String key,String value){
//       return redisTemplate.opsForZSet().score(key,value);
//    }
//
//    public void remove(String key){
////        删除在这之间的数据
//        redisTemplate.opsForZSet().removeRangeByScore(key,0,System.currentTimeMillis());
//    }

    public void addWithTimestampScore(String key, String value, long expirationMillis) {
        if (key == null || value == null) {
            return; // 防止空值写入 Redis
        }

        double score = System.currentTimeMillis() + expirationMillis; // 设置过期时间戳作为 score
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取某个成员的 score（即过期时间戳）
     */
    public Double getScore(String key, String value) {
        if (key == null || value == null) {
            return null;
        }

        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 删除所有已经过期的成员（score <= 当前时间戳）
     */
    public void removeExpired(String key) {
        if (key == null) {
            return;
        }

        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, now);
    }

    /**
     * 获取所有未过期的成员
     */
    public Set<String> getNonExpiredMembers(String key) {
        if (key == null) {
            return null;
        }

        long now = System.currentTimeMillis();
        return redisTemplate.opsForZSet().rangeByScore(key, now, Double.MAX_VALUE);
    }
}

