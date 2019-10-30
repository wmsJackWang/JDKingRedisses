package org.redisses.jdking.Lock;

import org.redisses.jdking.client.JDKingRedisClient;
import org.redisses.jdking.client.JDKingResultInfo;

/**
 * Hello world!
 *
 */
public class JackDKingRedisDistributedKey 
{
   

    public static long defaultExpireTime = 6000;
    
	//remove key
	public static void remove(String key) {
		JDKingResultInfo<Long> resultInfo = JDKingRedisClient.doWithOut((jedis)->{
			jedis.del(key);
		});
	}
	
	
	public static Object get(String key) {
		JDKingResultInfo<String> resultInfo = JDKingRedisClient.domain((jedis)->{
			if(null == key||key.equals(""))
				return null;
			return jedis.get(key);
		});
		if(resultInfo.isSuccess())
			return resultInfo.getResultObj();
		else
			return null;
	}
	
	public static boolean setNx(String key , Long expireSeconds) {
//		boolean result ;
		//原作者使用这个来尝试开发分布式锁？？？ 
		//这个jdk同步器是用来开发同一个进程内的多线程同步的工具。。。，所以这是误用。
		//我还要指出这里的问题: 这里的原子操作设计是错误的。对于redis来说这整个代码块不是原子操作
//		synchronized(this) {
//		if(jedis.exists(key))
//			result = false;
//		else {
//				//当判断key不存在而进入这个代码块的过程中，redis的锁可能已经被获取了，这个时候就不能设置result为true
//				jedis.setex(key, expireSeconds.intValue()/1000, "1");
//				result =  true;
//			}
//		}
		JDKingResultInfo<Boolean> resultInfo = JDKingRedisClient.domain((jedis)->{
			
			long ret = jedis.setnx(key, "1");
			if(ret==1)
			{
				//为已经获取的锁，设置过期时间
				long expireResult = jedis.expire(key, (int) (defaultExpireTime/1000));
				//expireResult==0的情况是为了支持Redis versions <2.1.3情形下，过期时间存在则不会设置，之后的版本则会覆盖过期时间
				if(expireResult==0)
					return false;
				else
					return true;
			}
			else
				return false;
		});
		if(resultInfo.getResultObj()==null)
			return false;
		
		return resultInfo.getResultObj()?true:false;
	}
	
	public static boolean lock(String key , Long expireSeconds,Long waitSeconds)
	{
		boolean result ;
		result = setNx(key, expireSeconds);
		if(result)return result;
		else {
			//锁获取开始时间点
			Long curent = System.currentTimeMillis();
			//锁获取结束时间点
			Long outTime = curent + waitSeconds;
			
			while(System.currentTimeMillis()<outTime) {//对比当前时间是否还在过期时间点内
				boolean resultboolean =  setNx(key, expireSeconds);
				if(resultboolean)return resultboolean;
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(" 锁获取超时，失败");
		}
		
		return false;
		
	}
	
}
