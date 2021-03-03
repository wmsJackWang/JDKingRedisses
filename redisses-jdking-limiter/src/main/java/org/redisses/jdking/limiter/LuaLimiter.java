package org.redisses.jdking.limiter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.redisses.jdking.client.JDKingRedisClient;

/**
 * Hello world!
 *
 */
public class LuaLimiter 
{
    public static String lua ;
    
    
    //类加载进jvm的时候就执行初始化方法init
    static {
    	init();
    }
	
    
    public static void init() {
   	 
   	 try {
   		 InputStream inputStream = Objects.requireNonNull(
   				LuaLimiter.class.getClassLoader().getResourceAsStream("limiter.lua"));
   		 lua = readFile(inputStream);
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
    
    public static void main( String[] args )
    {
    	JDKingRedisClient.doWithOut((jedis)->{
			
			System.out.println("进入方法");
	        List<String> keys = new ArrayList<>();
	        keys.add("ip:limit:127.0.0.1");
	        List<String> arggs = new ArrayList<>();
	        arggs.add("1000");//限流的时间范围
	        arggs.add("10");//限流  数字为4 
	        //两个参数的含义： 6s内 只允许 4个请求 通过。
	        //下面方法是返回  lua脚本在redis中的缓存id。后续通过id来执行脚本，避免每次执行都传  lua 代码
        	String luaLoad = jedis.scriptLoad(lua);
        	System.out.println("lua脚本缓存在redis中的id是:"+luaLoad);
        	System.out.println("开始每500ms发起一次 redis请求");
	        for(int i =0 ; ; i++)
	        {
		        Object obj = jedis.evalsha(luaLoad,keys,arggs);
		        System.out.println("第"+i+"个10微秒时刻执行脚本返回结果:"+obj);
		        try {
					TimeUnit.MICROSECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
			
		});
    }
}
