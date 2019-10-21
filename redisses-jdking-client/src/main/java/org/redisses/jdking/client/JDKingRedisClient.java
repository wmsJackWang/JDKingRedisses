package org.redisses.jdking.client;

import com.jsondream.redisses.client.domainInterfaces.RedisDoWithOutInterface;
import com.jsondream.redisses.client.domainInterfaces.RedisDomainInterface;

import redis.clients.jedis.Jedis;

public class JDKingRedisClient {

    public static <T extends Object> JDKingResultInfo<T> domain(RedisDomainInterface<T> interfaces) {
        T Object;
        JDKingResultInfo<T> result = new JDKingResultInfo<T>();
        Jedis jedis = null;
        try{
        	jedis = JDKingRedisPool.getJedis();
            Object = interfaces.domain(jedis);
            //只有成功的时候才会执行下面的代码段 
        	result.setSuccess(true);
        	result.setResultObj(Object);
        	result.setErrMsg(null);
        	result.setException(null);
        }catch (Exception e) {
			// TODO: handle exception
        	result.setSuccess(false);
        	result.setResultObj(null);
        	result.setErrMsg(e.getClass().getName());
        	result.setException(e);
		}finally {
			try {
				JDKingRedisPool.returnResource(jedis);
			} catch (Exception e2) {
				// TODO: handle exception
				// TODO: handle exception
	        	result.setErrMsg(result.getErrMsg()+" , jedis连接资源回收异常:"+ e2.getClass().getName());
			}
			
        }
        return result;
    }

    public static <T  extends Object>  JDKingResultInfo<T>  doWithOut(RedisDoWithOutInterface interfaces) {
        Jedis jedis = null ;
        JDKingResultInfo<T> result = new JDKingResultInfo<T>();
        
        try {
        	jedis = JDKingRedisPool.getJedis();
            interfaces.domain(jedis);
            result.setSuccess(true);
        	result.setResultObj(null);
        	result.setErrMsg(null);
        	result.setException(null);
        }catch (Exception e) {
			// TODO: handle exception
        	result.setSuccess(false);
        	result.setResultObj(null);
        	result.setErrMsg(e.getClass().getSimpleName());
        	result.setException(e);
		} finally {
			try {
				JDKingRedisPool.returnResource(jedis);
			} catch (Exception e2) {
				// TODO: handle exception
				// TODO: handle exception
	        	result.setSuccess(false);
	        	result.setResultObj(null);
	        	result.setErrMsg(result.getErrMsg()+" , "+ e2.getClass().getSimpleName());
	        	result.setException(e2);
			}
        }

        return result;
    }
	
}
