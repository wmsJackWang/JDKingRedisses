package org.redisses.jdking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisses.jdking.client.JDKingRedisClient;

public class LuaSample {
	
    private final static String lua = "local num=redis.call('incr',KEYS[1])\n" +
            "if tonumber(num)==1 then\n" +
            "\tredis.call('pexpire',KEYS[1],ARGV[1])\n" +
            "\treturn 1\n" +
            "elseif tonumber(num)>tonumber(ARGV[2]) then\n" +
            "\treturn 0\n" +
            "else \n" +
            "\treturn 1\n" +
            "end";
	
	public static void main(String[] args) {
		System.out.println("测试jedis的lua脚本执行api");
//		testLua();
		testEvalshaLua();
	}

	private static void testEvalshaLua() {
		// TODO Auto-generated method stub

		JDKingRedisClient.doWithOut((jedis)->{
			
			System.out.println("进入方法");
	        List<String> keys = new ArrayList<>();
	        keys.add("ip:limit:127.0.0.1");
	        List<String> arggs = new ArrayList<>();
	        arggs.add("6000");//限流的时间范围
	        arggs.add("4");//限流  数字为4 
	        //两个参数的含义： 6s内 只允许 4个请求 通过。
	        //下面方法是返回  lua脚本在redis中的缓存id。后续通过id来执行脚本，避免每次执行都传  lua 代码
        	String luaLoad = jedis.scriptLoad(lua);
        	System.out.println("lua脚本缓存在redis中的id是:"+luaLoad);
	        for(int i =1 ; ; i++)
	        {
	        	i%=12;
	        	if(i%12==0)
	        		System.out.println("6s流量统计数目清零，重新计算");
	        	
		        Object obj = jedis.evalsha(luaLoad,keys,arggs);
		        System.out.println("第"+i+"个500ms时刻执行脚本返回结果:"+obj);
		        try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
			
		});
	}

	private static void testEvalLua() {
		// TODO Auto-generated method stub

		JDKingRedisClient.doWithOut((jedis)->{
			
			System.out.println("进入方法");
	        List<String> keys = new ArrayList<>();
	        keys.add("ip:limit:127.0.0.1");
	        List<String> arggs = new ArrayList<>();
	        arggs.add("6000");//限流的时间范围
	        arggs.add("4");//限流  数字为4 
	        //两个参数的含义： 6s内 只允许 4个请求 通过。
	        for(int i =1 ; ; i++)
	        {
	        	i%=12;
		        Object obj = jedis.eval(lua,keys,arggs);
		        System.out.println("第"+i+"个500ms时刻执行脚本返回结果:"+obj);
		        try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
			
		});
	}

}
