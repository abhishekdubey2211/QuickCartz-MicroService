package com.shopify.service.redis.connection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisCommonMethods {
	private static final Logger log = LoggerFactory.getLogger(RedisCommonMethods.class);
	private final JedisPool jedisPool;
	private static final int DEFAULT_EXPIRY = (int) TimeUnit.HOURS.toSeconds(24);
	private static final Gson GSON = new Gson(); // Properly instantiate Gson

	public RedisCommonMethods(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public void set(String key, String value, Integer expiry) {
		try (Jedis jedis = jedisPool.getResource()) {
			int expireTime = (expiry != null) ? expiry : DEFAULT_EXPIRY;
			jedis.set(key, value);
			jedis.expire(key, expireTime);
		} catch (JedisException e) {
			log.error("Error setting key {} in Redis: ", key, e);
		}
	}

	public String get(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.get(key);
		} catch (JedisException e) {
			log.error("Error getting key {} from Redis: ", key, e);
			return null;
		}
	}

	public void delete(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.del(key);
		} catch (JedisException e) {
			log.error("Error deleting key {} from Redis: ", key, e);
		}
	}

	public boolean exists(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.exists(key);
		} catch (JedisException e) {
			log.error("Error checking existence of key {} in Redis: ", key, e);
			return false;
		}
	}

	public void hset(String key, String field, String value, Integer expiry) {
		try (Jedis jedis = jedisPool.getResource()) {
			int expireTime = (expiry != null) ? expiry : DEFAULT_EXPIRY;
			jedis.hset(key, field, value);
			jedis.expire(key, expireTime);
		} catch (JedisException e) {
			log.error("Error setting hash field {} in key {} in Redis: ", field, key, e);
		}
	}

	public String hget(String key, String field) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hget(key, field);
		} catch (JedisException e) {
			log.error("Error getting hash field {} from key {} in Redis: ", field, key, e);
			return null;
		}
	}

	public void hdel(String key, String field) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.hdel(key, field);
		} catch (JedisException e) {
			log.error("Error deleting hash field {} from key {} in Redis: ", field, key, e);
		}
	}

	public void publish(String channel, String message) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.publish(channel, message);
		} catch (JedisException e) {
			log.error("Error publishing message to channel {} in Redis: ", channel, e);
		}
	}

	public void subscribe(String channel, JedisPubSub subscriber) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.subscribe(subscriber, channel);
		} catch (JedisException e) {
			log.error("Error subscribing to channel {} in Redis: ", channel, e);
		}
	}
	public List<String>  getAllChannels() {
		try (Jedis jedis = jedisPool.getResource()) {
			return  jedis.pubsubChannels();
		} catch (JedisException e) {
			log.error("Error retrieving all Redis pub/sub channels: ", e);
			return null;
		}
	}


	public void flushAll() {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.flushAll();
		} catch (JedisException e) {
			log.error("Error flushing Redis: {}", e.getMessage(), e);
		}
	}

	public void jsonSet(String key, String jsonValue, Integer expiry) {
		try (Jedis jedis = jedisPool.getResource()) {
			int expireTime = (expiry != null) ? expiry : DEFAULT_EXPIRY;
			jedis.set(key, jsonValue); // Simulating RedisJSON functionality
			jedis.expire(key, expireTime);
		} catch (JedisException e) {
			log.error("Error setting JSON value in key {}: ", key, e);
		}
	}

	public String jsonGet(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.get(key); // Simulating RedisJSON functionality
		} catch (JedisException e) {
			log.error("Error getting JSON value from key {}: ", key, e);
			return null;
		}
	}

	public void jsonDel(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.del(key); // Simulating RedisJSON functionality
		} catch (JedisException e) {
			log.error("Error deleting JSON value from key {}: ", key, e);
		}
	}

	private static String convertObjToString(Object obj) {
		return GSON.toJson(obj);
	}

	public static <T> T convertStringToObj(String jsonString, Class<T> clazz) {
		try {
			return GSON.fromJson(jsonString, clazz);
		} catch (Exception e) {
			log.error("Error converting JSON string to object: {}", e.getMessage(), e);
			return null;
		}
	}

	public static <T> T convertStringToObjType(String jsonString, TypeToken<T> typeToken) {
		try {
			return GSON.fromJson(jsonString, typeToken.getType());
		} catch (Exception e) {
			log.error("Error converting JSON string to object type: {}", e.getMessage(), e);
			return null;
		}
	}
}
