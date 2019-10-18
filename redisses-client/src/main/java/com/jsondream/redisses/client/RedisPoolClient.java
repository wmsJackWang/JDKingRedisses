package com.jsondream.redisses.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * <p>
 * redis杩炴帴姹犲鎴风绠＄悊绫�
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 15/12/23
 */
public class RedisPoolClient {

    private RedisPoolClient() {
    }

    public static RedisPoolClient getInstance() {
        return LazyHolder.redisPoolClient;
    }

    private static class LazyHolder {
        private static final RedisPoolClient redisPoolClient = new RedisPoolClient();
    }

    // 杩炴帴姹�
    private static JedisPool pool;

    //閰嶇疆鏂囦欢鐨勯厤缃俊鎭幏鍙栨柟寮�
    //鍏ㄦ柊鐨勪竴绉嶈幏鍙杙roperties鏂囦欢鍙傛暟鏁版嵁鐨勬柟寮忥紝鐚滄兂锛氳繖涓槸灏佽浜唒roperties宸ュ叿
    private static final ResourceBundle bundle = ResourceBundle.getBundle("redis");

    public void initPool() {
        if (bundle == null) {
            throw new IllegalArgumentException("[redis.properties] is not found!");
        }
        // redis閰嶇疆淇℃伅
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.valueOf(bundle.getString("redis.pool.maxTotal")));
        config.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
        config.setMaxWaitMillis(Long.valueOf(bundle.getString("redis.pool.maxWait")));
        config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
        config.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));

        // sohu-test-redis杩炴帴淇℃伅
//        pool = new JedisPool(config, bundle.getString("redis.ip"),
//            Integer.valueOf(bundle.getString("redis.port")),
//            Integer.valueOf(bundle.getString("redis.connectionOutTime")),
//            bundle.getString("redis.auth"));
        
        // my-test-redis杩炴帴淇℃伅 锛屾棤瀵嗙爜鍒濆鍖�
        pool = new JedisPool(config, bundle.getString("redis.ip"),
                Integer.valueOf(bundle.getString("redis.port")),
                Integer.valueOf(bundle.getString("redis.connectionOutTime")));
    }

        public static void main(String[] args) {
            RedisPoolClient.getInstance().initPool();
            // 浠庢睜涓幏鍙栦竴涓狫edis瀵硅薄
            Jedis jedis = RedisPoolClient.getInstance().getJedis();
            String keys = "name";
            // 鍒犳暟鎹�
            jedis.del(keys);
            // 瀛樻暟鎹�
            jedis.set(keys, "snowolf");
            // 鍙栨暟鎹�
            String value = jedis.get(keys);
    
            Map<String, String> map = new HashMap<>();
            map.put("sc", "sb");
            //        jedis.hmset("map", map);
            jedis.hdel("game", "sc");
            System.out.println(map == null ? 1 : 2);
            System.out.println(jedis.hgetAll("map"));
            System.out.println(jedis.hgetAll("game"));
            System.out.println(value);
            System.out.println(jedis.hget("map", "sc"));
            jedis.hdel("map", "sc");
            System.out.println(jedis.hgetAll("map"));
            System.out.println(jedis.hget("map", "sc"));
    
        }

//    public static void main(String[] args) {
//        RedisPoolClient.getInstance().initPool();
//        Jedis jedis = RedisPoolClient.getInstance().getJedis();
//
//        jedis.zadd("hehu", new Date().getTime(), "09987:123");
//        jedis.zadd("hehu", 12, "4567:1");
//        jedis.zadd("hehu", 234, "345:32");
//
//       // jedis.zrem("hehu","345");
//
//        //jedis.zrem("hehu","09987:");
//
////        Set<String> ss = jedis.zrangeByLex("hehu","(09987:","-");
////        Iterator<String> t = ss.iterator();
////        while(t.hasNext()){
////            System.out.println(t.next());
////        }
//
//
//
//        jedis.zremrangeByLex("hehu","[09987:123","[锛�");
//        Set<String> s = jedis.zrevrange("hehu", 0, 10);
//        System.out.println("闆嗗悎鐨勬暟鎹釜鏁�: "+s.size());
//
//
//
//
//        Iterator<String> it = s.iterator();
//        
//        while(it.hasNext()){
//            System.out.println(it.next());
//        }
//
//
//    }

    /**
     * 鑾峰彇Jedis瀹炰緥
     *
     * @return
     */
    public synchronized Jedis getJedis() {
        try {
            if (pool != null) {
                Jedis resource = pool.getResource();
                return resource;
            } else {
                // 鑵捐浜戠殑redis,3涓皬鏃舵病鏈夋暟鎹紶杈撳氨浼氭柇寮�闀胯繛鎺�,杩欓噷鏄负浜嗛噸鏂板缓绔嬮暱杩炴帴
                initPool();
                return pool != null ? pool.getResource() : null;
            }
        } catch (Exception e) {
            //TODO: connect error log鐨勮褰�....
        }
        return null;
    }

    /**
     * 閲婃斁jedis璧勬簮
     *
     * @param jedis
     */
    public void returnResource(final Jedis jedis) {
        if (jedis != null && pool != null) {
            pool.returnResourceObject(jedis);
        }
    }

    /**
     * 閲婃斁瀵硅薄姹�
     */
    public void destroy() {
        synchronized (pool) {
            if (pool != null) {
                pool.destroy();
            }
        }
    }
}
