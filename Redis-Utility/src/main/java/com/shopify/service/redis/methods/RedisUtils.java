/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shopify.service.redis.methods;

/**
 *
 * @author abhishek.d
 */
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RedisUtils {

    private final JedisPool jedisPool;
    private final Gson gson = new Gson();
    private static final long DEFAULT_EXPIRATION_TIME = 24 * 60 * 60; // 24 hours in seconds

    public RedisUtils(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setWithDefaultExpiration(String key, Object value) {
        setWithExpiration(key, value, DEFAULT_EXPIRATION_TIME, TimeUnit.SECONDS);
    }

    public void setWithExpiration(String key, Object value, long timeout, TimeUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonValue = convertObjToString(value);
            jedis.setex(key, (int) unit.toSeconds(timeout), jsonValue);
        } catch (Exception e) {
            throw new RuntimeException("Error setting value in Redis", e);
        }
    }

    public void set(String key, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonValue = convertObjToString(value);
            jedis.set(key, jsonValue);
        } catch (Exception e) {
            throw new RuntimeException("Error setting value in Redis", e);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            throw new RuntimeException("Error getting value from Redis", e);
        }
    }

    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting key from Redis", e);
        }
    }

    public void setHashFieldWithDefaultExpiration(String key, String field, Object value) {
        setHashFieldWithExpiration(key, field, value, DEFAULT_EXPIRATION_TIME, TimeUnit.SECONDS);
    }

    public void setHashFieldWithExpiration(String key, String field, Object value, long timeout, TimeUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonValue = convertObjToString(value);
            jedis.hset(key, field, jsonValue);
            jedis.expire(key, (int) unit.toSeconds(timeout));
        } catch (Exception e) {
            throw new RuntimeException("Error setting hash field in Redis", e);
        }
    }

    public void setHashField(String key, String field, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonValue = convertObjToString(value);
            jedis.hset(key, field, jsonValue);
        } catch (Exception e) {
            throw new RuntimeException("Error setting hash field in Redis", e);
        }
    }

    public String getHashField(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        } catch (Exception e) {
            throw new RuntimeException("Error getting hash field from Redis", e);
        }
    }

    public Map<String, String> getHashEntries(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            throw new RuntimeException("Error getting all hash fields from Redis", e);
        }
    }

    public String convertObjToString(Object obj) {
        return gson.toJson(obj);
    }

    public <T> T convertStringToObj(String jsonString, Class<T> clazz) {
        Type type = TypeToken.get(clazz).getType();
        return gson.fromJson(jsonString, type);
    }

    public <T> List<T> convertStringToObjType(String jsonString, TypeToken<T> typeToken) {
        return gson.fromJson(jsonString, typeToken.getType());
    }
}
