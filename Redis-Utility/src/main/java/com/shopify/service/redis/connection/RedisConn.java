package com.shopify.service.redis.connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import com.shopify.service.redis.constants.ConstantData;
import com.shopify.service.redis.model.ServerDetails;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import redis.clients.jedis.exceptions.JedisException;

public class RedisConn {
    private static final Logger log = LoggerFactory.getLogger(RedisConn.class);
    private  JedisSentinelPool objSentinelPool;
    private  JedisPool objSinglePool;
    private int nType = 0;
    private ServerDetails objRedisServerDetails=
            new ServerDetails(1,"Redis Server", "127.0.0.1" , 6379 ,"Abhi@22112000");

    public RedisConn() {
        getRedisConn();
    }

    public void initializeRedis() {
        log.info("******* Initializing Redis ******* Version: {}", ConstantData.strRedisVersion);

        final Set<String> sentinels = new HashSet<>();
        nType = objRedisServerDetails.getConnectiontype();
        JedisPoolConfig objConfig = new JedisPoolConfig();
        objConfig.setMaxTotal(100);
        objConfig.setMaxIdle(40);
        objConfig.setMinIdle(5);
        objConfig.setTestOnBorrow(true);
        objConfig.setTestOnCreate(true);
        objConfig.setTestOnReturn(true);
        objConfig.setNumTestsPerEvictionRun(-1);
        int ntimeout = 2000;

        try {
            switch (nType) {
                case ConstantData.TYPE_SENTINAL:
                    sentinels.add(objRedisServerDetails.getIpAddress() + ":" + objRedisServerDetails.getPortno());
                    objSentinelPool = new JedisSentinelPool(
                            objRedisServerDetails.getLoginName(),
                            sentinels,
                            objConfig,
                            ntimeout,
                            objRedisServerDetails.getPassword()
                    );
                    log.info("Initialized Redis with sentinels.");
                    break;
                case ConstantData.TYPE_SINGLE:
                    objSinglePool = new JedisPool(
                            objConfig,
                            objRedisServerDetails.getIpAddress(),
                            objRedisServerDetails.getPortno(),
                            ntimeout,
                            objRedisServerDetails.getPassword()
                    );
                    log.info("Initialized Redis without sentinels.");
                    break;
                case ConstantData.TYPE_SINGLE_SECURE:
                    SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    SSLParameters sslParameters = new SSLParameters();
                    sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
                    log.debug("Configured SSL parameters for secure connection.");

                    objSinglePool = new JedisPool(
                            objConfig,
                            objRedisServerDetails.getIpAddress(),
                            objRedisServerDetails.getPortno(),
                            ntimeout,
                            objRedisServerDetails.getPassword(),
                            true
                    );
                    log.info("Initialized Redis with secure connection.");
                    break;
                default:
                    log.warn("Unknown Redis connection type: {}", nType);
                    return;
            }

            try (Jedis objJedis = getRedisConn()) {
                if (objJedis != null) {
                    log.info("Redis connection is alive: {}", objJedis.ping());
                } else {
                    log.error("Failed to establish Redis connection.");
                }
            }
        } catch (JedisException e) {
            log.error("JedisException occurred while initializing Redis: ", e);
        } catch (Exception e) {
            log.error("Unexpected exception occurred while initializing Redis: ", e);
        }
    }

    public Jedis getRedisConn() {
        log.info("Redis Version: {}, Connection Type: {}", getRedisHandlerVersion(), nType);
        try {
            if (nType == ConstantData.TYPE_SENTINAL || nType == ConstantData.TYPE_SENTINAL_SECURE) {
                if (objSentinelPool == null) {
                    initializeRedis();
                }
                return objSentinelPool != null ? objSentinelPool.getResource() : null;
            } 
//            else if (nType == ConstantData.TYPE_SINGLE || nType == ConstantData.TYPE_SINGLE_SECURE) {
//                if (objSinglePool == null) {
//                    initializeRedis();
//                }
//                return objSinglePool != null ? objSinglePool.getResource() : null;
//            }
            else{
                 if (objSinglePool == null) {
                    initializeRedis();
                }
                 return objSinglePool != null ? objSinglePool.getResource() : null;
            }
        } catch (JedisException e) {
            log.error("Error retrieving Redis connection: ", e);
        }
        return null;
    }

    public static String getRedisHandlerVersion() {
        return "Redis Handler v0.0.1 - 7th February 2025";
    }

    public void close() {
        try {
            if (objSentinelPool != null) {
                objSentinelPool.close();
                log.info("Closed JedisSentinelPool.");
            }
            if (objSinglePool != null) {
                objSinglePool.close();
                log.info("Closed JedisPool.");
            }
        } catch (JedisException e) {
            log.error("Error while closing Redis connection pools: ", e);
        }
    }
}
