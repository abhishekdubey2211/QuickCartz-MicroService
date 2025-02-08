package com.shopify.service.redis.methods;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shopify.service.redis.connection.RedisConn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RedisCommonMethods {
    private static final Logger log = LoggerFactory.getLogger(RedisCommonMethods.class);
    private final Jedis jedis; // Using Jedis directly, not JedisPool
    private final Gson gson = new Gson();
    private static final long DEFAULT_EXPIRATION_TIME = TimeUnit.DAYS.toSeconds(1); // 24 hours

    public RedisCommonMethods(Jedis jedis) {
        this.jedis = jedis; // Direct Jedis instance
    }

    // ====== Basic String Operations ======
    public void set(String key, Object value) {
        try {
            jedis.set(key, convertObjToString(value));
        } catch (Exception e) {
            log.error("Error setting key {} in Redis: {}", key, e.getMessage(), e);
        }
    }

    public void setWithExpiration(String key, Object value, long timeout, TimeUnit unit) {
        try {
            jedis.setex(key, (int) unit.toSeconds(timeout), convertObjToString(value));
        } catch (Exception e) {
            log.error("Error setting key {} with expiration in Redis: {}", key, e.getMessage(), e);
        }
    }

    public void setWithDefaultExpiration(String key, Object value) {
        setWithExpiration(key, value, DEFAULT_EXPIRATION_TIME, TimeUnit.SECONDS);
    }

    public String get(String key) {
        try {
            return jedis.get(key);
        } catch (Exception e) {
            log.error("Error retrieving key {} from Redis: {}", key, e.getMessage(), e);
            return null;
        }
    }

    public void delete(String key) {
        try {
            jedis.del(key);
        } catch (Exception e) {
            log.error("Error deleting key {} from Redis: {}", key, e.getMessage(), e);
        }
    }

    public boolean exists(String key) {
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("Error checking existence of key {} in Redis: {}", key, e.getMessage(), e);
            return false;
        }
    }

    // ====== Hash Operations ======
    public void setHashField(String key, String field, Object value) {
        try {
            jedis.hset(key, field, convertObjToString(value));
        } catch (Exception e) {
            log.error("Error setting hash field {} for key {} in Redis: {}", field, key, e.getMessage(), e);
        }
    }

    public void setHashFieldWithExpiration(String key, String field, Object value, long timeout, TimeUnit unit) {
        try {
            jedis.hset(key, field, convertObjToString(value));
            jedis.expire(key, (int) unit.toSeconds(timeout));
        } catch (Exception e) {
            log.error("Error setting hash field {} for key {} in Redis: {}", field, key, e.getMessage(), e);
        }
    }

    public String getHashField(String key, String field) {
        try {
            return jedis.hget(key, field);
        } catch (Exception e) {
            log.error("Error retrieving hash field {} for key {} from Redis: {}", field, key, e.getMessage(), e);
            return null;
        }
    }

    public Map<String, String> getHashEntries(String key) {
        try {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("Error retrieving hash fields for key {} from Redis: {}", key, e.getMessage(), e);
            return null;
        }
    }

    public boolean isHashFieldExists(String key, String field) {
        try {
            return jedis.hexists(key, field);
        } catch (Exception e) {
            log.error("Error checking existence of hash field {} for key {} in Redis: {}", field, key, e.getMessage(),
                    e);
            return false;
        }
    }

    // ====== Serialization Methods ======
    private String convertObjToString(Object obj) {
        return gson.toJson(obj);
    }

    public <T> T convertStringToObj(String jsonString, Class<T> clazz) {
        Type type = TypeToken.get(clazz).getType();
        return gson.fromJson(jsonString, type);
    }

    public <T> List<T> convertStringToObjType(String jsonString, TypeToken<T> typeToken) {
        return gson.fromJson(jsonString, typeToken.getType());
    }

    public static void main(String[] args) {
        RedisConn redis = new RedisConn();
        Jedis jedis = redis.getRedisConn(); // Get Jedis instance

        RedisCommonMethods redisMethods = new RedisCommonMethods(jedis);

        // Test basic operations
        redisMethods.set("testKey", "Hello, Redis!");
        System.out.println("Stored value: " + redisMethods.get("testKey"));

        redisMethods.setWithExpiration("tempKey", "Expiring soon", 10, TimeUnit.SECONDS);
        System.out.println("Temp key exists: " + redisMethods.exists("tempKey"));

        redisMethods.setHashField("user:1001", "name", "John Doe");
        System.out.println("Hash field value: " + redisMethods.getHashField("user:1001", "name"));

        redisMethods.delete("testKey");
        System.out.println("Exists after deletion: " + redisMethods.exists("testKey"));

        // Close Jedis connection
        jedis.close();
    }
}
