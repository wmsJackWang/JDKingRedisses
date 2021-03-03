package org.redisses.jackdking.jiankucun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.redisses.jdking.client.JDKingRedisClient;
import org.redisses.jdking.client.JDKingResultInfo;


/**
 * Hello world!
 *
 */
public class StockReduce 
{
	
	private static String stockReduceLuaScript ;
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
    				 StockReduce.class.getClassLoader().getResourceAsStream("StockReduceScript.lua"));
    		 stockReduceLuaScript = readFile(inputStream);
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    
    private static Long reduceStock(String key,Long orderid , Long reduceValue) {
		// TODO Auto-generated method stub
    		JDKingResultInfo<Long> resultInfo = JDKingRedisClient.domain((jedis)->{
    		
//    		List<String> keyList = Collections.singletonList(key);
    		List<String> keyList = Arrays.asList(key,orderid+"");

            List<String> argsList = Collections.singletonList(reduceValue+"");
			
			long ret = (long) jedis.eval(stockReduceLuaScript, keyList, argsList);
			if(ret==-1)
				return -1L; 
			else
				return ret;
		});
		if(resultInfo.getResultObj()==null)
		{
			resultInfo.getException().printStackTrace();
			return -1L;
		}
		
		return resultInfo.getResultObj();
	}
    
    private static void setStock(String key ,Long stock)
    {
    
    	JDKingRedisClient.doWithOut((jedis)->{
    		jedis.set(key, stock+"");
    	});
    }

    
    public static void main(String[] args) {
    	
//    	setStock("testkey",1000L);
    	System.out.println("success");
//    	setStock("testkey",100L);
    	System.out.println(reduceStock("testkey1", System.currentTimeMillis(), 10L));
	}
    
}
