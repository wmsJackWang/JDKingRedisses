package org.redisses.jdking.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JDKingRedisPool {

	private static JedisPool jedisPool = null; 
	
	static{
		Properties properties=new Properties();
		try {
			//直接写src 类路径下的文件名
			InputStream input=JDKingRedisPool.class.getClassLoader().getResourceAsStream("jdkingredisses.properties");  
			
			properties.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JedisPoolConfig config = new JedisPoolConfig();
        config.setBlockWhenExhausted(Boolean.valueOf(properties.getProperty("redisses.jdking.config.BLOCK_WHEN_EXHAUSTED")));
        config.setEvictionPolicyClassName(properties.getProperty("redisses.jdking.config.EVICTION_POLICY_CLASSNAME")); 
        config.setJmxEnabled(Boolean.valueOf(properties.getProperty("redisses.jdking.config.JMX_ENABLED")));
        config.setJmxNamePrefix(properties.getProperty("redisses.jdking.config.JMX_NAME_PREFIX"));
        config.setLifo(Boolean.valueOf(properties.getProperty("redisses.jdking.config.LIFO")));
        config.setMaxIdle(Integer.valueOf(properties.getProperty("redisses.jdking.config.MAX_IDLE")));
        config.setMaxTotal(Integer.valueOf(properties.getProperty("redisses.jdking.config.MAX_TOTAL")));
        config.setMaxWaitMillis(Long.valueOf(properties.getProperty("redisses.jdking.config.MAX_WAIT")));
        config.setMinEvictableIdleTimeMillis(Long.valueOf(properties.getProperty("redisses.jdking.config.MIN_EVICTABLE_IDLE_TIME_MILLIS")));
        config.setMinIdle(Integer.valueOf(properties.getProperty("redisses.jdking.config.MIN_IDLE")));
        config.setNumTestsPerEvictionRun(Integer.valueOf(properties.getProperty("redisses.jdking.config.NUM_TESTS_PER_EVICYION_RUN")));
        config.setSoftMinEvictableIdleTimeMillis(Long.valueOf(properties.getProperty("redisses.jdking.config.SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS")));
        config.setTestOnBorrow(Boolean.valueOf(properties.getProperty("redisses.jdking.config.TEST_ON_BORROW")));
        config.setTestWhileIdle(Boolean.valueOf(properties.getProperty("redisses.jdking.config.TEST_WHILEIDLE")));
        config.setTimeBetweenEvictionRunsMillis(Long.valueOf(properties.getProperty("redisses.jdking.config.TIME_BERWEEN_EVICTION_RUNS_MILLIS")));
        
        //敏感信息，开源的时候一注释掉
        String SOHU_ADDR = properties.getProperty("redisses.jdking.client.sohu.ip");
        int SOHU_PORT = Integer.valueOf(properties.getProperty("redisses.jdking.client.sohu.port"));
        String SOHU_AUTH = properties.getProperty("redisses.jdking.client.sohu.auth");
        
        String JACKDKING_ADDR = properties.getProperty("redisses.jdking.client.jdking.ip");
        int JACKDKING_PORT = Integer.valueOf(properties.getProperty("redisses.jdking.client.jdking.port"));
        String JACKDKING_AUTH = properties.getProperty("redisses.jdking.client.jdking.auth");
        
        int TIMEOUT = Integer.valueOf(properties.getProperty("redisses.jdking.client.timeout"));
        
        jedisPool = new JedisPool(config, JACKDKING_ADDR, JACKDKING_PORT, TIMEOUT);
        
//        jedisPool = new JedisPool(config, SOHU_ADDR, SOHU_PORT,TIMEOUT, SOHU_AUTH);
	}
	
	/**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
    /**
     * 释放jedis连接资源
     * @param jedis
     */
    public static void close(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
    
    
    /**
     * 归还redis连接给redis连接池
     *
     * @param jedis
     */
    public void returnResource(final Jedis jedis) {
        if (jedisPool != null && jedisPool != null) {
        	jedisPool.returnResourceObject(jedis);
        }
    }
    
    /**
     * 结束的时候销毁redis连接池资源
     */
    public void destroy() {
        synchronized (jedisPool) {
            if (jedisPool != null) {
            	jedisPool.destroy();
            }
        }
    }
}

