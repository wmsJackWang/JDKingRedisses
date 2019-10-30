package org.redisses.jdking.luaLock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.redisses.jdking.client.JDKingRedisClient;
import org.redisses.jdking.client.JDKingResultInfo;

/**
 * Hello world!
 *
 */
/**
 * @author mingshengwang
 * @date 2019年10月23日 下午5:08:37
 * @todo TODO
 * @descript 这个lua脚本实现redis分布式锁，因为在JackDKingRedisDistributedKey的上锁函数setNx和expire两个redis命令
 * 			  是由业务线程执行，而这个redis锁之所以保证了高可用，是因为这个redis锁是个设置了过期时间的key，客户端线程上锁后死亡导致锁key没有
 * 			 删除，但因为有了过期时间，因此过期后，其他业务线程也能使用这个锁。保障了锁的高可用，避免出现单点风险，但在JackDKingRedisDistributedKey
 * 			并没有完全避免单点风险。
 * 			 
 * 			问题： 因为setNx 和 expire两个命令不是原子性的，因此当线程执行在两个命令之间的时间段 突然死亡后，会造成这个锁没有办法使用。这个锁会出现单点故障
 * 			解决方案：1.使用lua脚本，将着两个命令作为lua脚本的一部分，作为一个整体命令来执行。保证了操作的原子性。
 * 					2.使用redis的 事务机制，将这两个命令作为原子操作执行。		
 * 
 * 		
 */
public class JackDKingLuaDistributedKey 
{
    public static long defaultExpireTime = 6000;
    
    public static String lockLua ;
    
    public static String unlockLua ;
    
    
    //类加载进jvm的时候就执行初始化方法init
    static {
    	init();
    }
    
    
//    getResourceAsStream有以下几种： 
//    1. Class.getResourceAsStream(String path) ： path 不以’/'开头时默认是从此类所在的包下取资源，以’/'开头则是从
//
//    ClassPath根下获取。其只是通过path构造一个绝对路径，最终还是由ClassLoader获取资源。
//
//    2. Class.getClassLoader.getResourceAsStream(String path) ：默认则是从ClassPath根下获取，path不能以’/'开头，最终是由ClassLoader获取资源。
//
//    3. ServletContext. getResourceAsStream(String path)：默认从WebAPP根目录下取资源，Tomcat下path是否以’/'开头无所谓，
    public static void init() {
    	 
    	 try {
    		 InputStream inputStream = Objects.requireNonNull(
        			 JackDKingLuaDistributedKey.class.getClassLoader().getResourceAsStream("jackdkingLock.lua"));
			 lockLua = readFile(inputStream);
			 
			 inputStream = Objects.requireNonNull(
        			 JackDKingLuaDistributedKey.class.getClassLoader().getResourceAsStream("jackdkingUnlock.lua"));
			 unlockLua = readFile(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public static void unlock(String key) {
		
		JDKingRedisClient.doWithOut((jedis)->{
			List<String> keyList = Collections.singletonList(key); 
			jedis.eval(unlockLua, keyList,Collections.EMPTY_LIST);
			
		});
	}

    private static boolean setNx(String key, Long expireSeconds) {
		// TODO Auto-generated method stub
    		JDKingResultInfo<Boolean> resultInfo = JDKingRedisClient.domain((jedis)->{
    		
    		List<String> keyList = Collections.singletonList(key);

            List<String> argsList = Collections.singletonList(expireSeconds+"");
			
			long ret = (long) jedis.eval(lockLua, keyList, argsList);
			if(ret==1)
				return true; 
			else
				return false;
		});
		if(resultInfo.getResultObj()==null)
		{
			resultInfo.getException().printStackTrace();
			return false;
		}
		
		return resultInfo.getResultObj()?true:false;
	}

    
    
	private static String readFile(InputStream inputStream) throws IOException {
        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line)
                    .append(System.lineSeparator());
            }

            return stringBuilder.toString();
        } 
    }
}


