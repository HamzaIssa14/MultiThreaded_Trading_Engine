package com.amazonaws.engine.cache;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CacheConnectionPool {
    private final JedisPool pool;

    public CacheConnectionPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(50);
        this.pool = new JedisPool(poolConfig, "localhost", 6379);
    }

    public JedisPool getPool(){
        return this.pool;
    }
}
