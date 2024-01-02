package com.amazonaws.engine.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CacheConnectionPool {
    private final JedisPool pool;
    private final String redisHost = "localhost"; // Ideally, these should be externalized
    private final int redisPort = 6379;

    public CacheConnectionPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(50);
        this.pool = new JedisPool(poolConfig, redisHost, redisPort);

        // Verify connection
        try (Jedis jedis = pool.getResource()) {
            String pingResponse = jedis.ping();
            if ("PONG".equals(pingResponse)) {
                System.out.println("Successfully connected to Redis at: " + redisHost + ":" + redisPort);
            } else {
                System.out.println("Failed to connect to Redis at: " + redisHost + ":" + redisPort);
            }
        } catch (Exception e) {
            System.out.println("Redis connection test failed: " + e.getMessage());
        }
    }

    public JedisPool getPool() {
        return this.pool;
    }
}
