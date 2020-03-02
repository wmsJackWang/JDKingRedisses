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
 * redis鏉╃偞甯村Ч鐘差吂閹撮顏粻锛勬倞缁拷
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

    // 鏉╃偞甯村Ч锟�
    private static JedisPool pool;

    //闁板秶鐤嗛弬鍥︽閻ㄥ嫰鍘ょ純顔讳繆閹垵骞忛崣鏍ㄦ煙瀵拷
    //閸忋劍鏌婇惃鍕缁夊秷骞忛崣鏉檙operties閺傚洣娆㈤崣鍌涙殶閺佺増宓侀惃鍕煙瀵骏绱濋悮婊勫厒閿涙俺绻栨稉顏呮Ц鐏忎浇顥婃禍鍞抮operties瀹搞儱鍙�
    private static final ResourceBundle bundle = ResourceBundle.getBundle("redis");

    public void initPool() {
        if (bundle == null) {
            throw new IllegalArgumentException("[redis.properties] is not found!");
        }
        // redis闁板秶鐤嗘穱鈩冧紖
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.valueOf(bundle.getString("redis.pool.maxTotal")));
        config.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
        config.setMaxWaitMillis(Long.valueOf(bundle.getString("redis.pool.maxWait")));
        config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
        config.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));

        // sohu-test-redis鏉╃偞甯存穱鈩冧紖
//        pool = new JedisPool(config, bundle.getString("redis.ip"),
//            Integer.valueOf(bundle.getString("redis.port")),
//            Integer.valueOf(bundle.getString("redis.connectionOutTime")),
//            bundle.getString("redis.auth"));
        
        // my-test-redis鏉╃偞甯存穱鈩冧紖 閿涘本妫ょ�靛棛鐖滈崚婵嗩潗閸栵拷
        pool = new JedisPool(config, bundle.getString("redis.ip"),
                Integer.valueOf(bundle.getString("redis.port")),
                Integer.valueOf(bundle.getString("redis.connectionOutTime")));
    }

        public static void main(String[] args) {
            RedisPoolClient.getInstance().initPool();
            // 娴犲孩鐫滄稉顓″箯閸欐牔绔存稉鐙玡dis鐎电钖�
            Jedis jedis = RedisPoolClient.getInstance().getJedis();
            String keys = "name";
            // 閸掔姵鏆熼幑锟�
            jedis.del(keys);
            // 鐎涙ɑ鏆熼幑锟�
            jedis.set(keys, "snowolf");
            // 閸欐牗鏆熼幑锟�
            String value = jedis.get(keys);
            System.out.println("是否存在这个key(name):"+jedis.exists(keys));
    
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
//        jedis.zremrangeByLex("hehu","[09987:123","[閿涳拷");
//        Set<String> s = jedis.zrevrange("hehu", 0, 10);
//        System.out.println("闂嗗棗鎮庨惃鍕殶閹诡喕閲滈弫锟�: "+s.size());
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
     * 閼惧嘲褰嘕edis鐎圭偘绶�
     *
     * @return
     */
    public synchronized Jedis getJedis() {
        try {
            if (pool != null) {
                Jedis resource = pool.getResource();
                return resource;
            } else {
                // 閼垫崘顔嗘禍鎴犳畱redis,3娑擃亜鐨弮鑸电梾閺堝鏆熼幑顔荤炊鏉堟挸姘ㄦ导姘焽瀵拷闂�鑳箾閹猴拷,鏉╂瑩鍣烽弰顖欒礋娴滃棝鍣搁弬鏉跨紦缁斿鏆辨潻鐐村复
                initPool();
                return pool != null ? pool.getResource() : null;
            }
        } catch (Exception e) {
            //TODO: connect error log閻ㄥ嫯顔囪ぐ锟�....
        }
        return null;
    }

    /**
     * 闁插﹥鏂乯edis鐠у嫭绨�
     *
     * @param jedis
     */
    public void returnResource(final Jedis jedis) {
        if (jedis != null && pool != null) {
            pool.returnResourceObject(jedis);
        }
    }

    /**
     * 闁插﹥鏂佺�电钖勫Ч锟�
     */
    public void destroy() {
        synchronized (pool) {
            if (pool != null) {
                pool.destroy();
            }
        }
    }
}
