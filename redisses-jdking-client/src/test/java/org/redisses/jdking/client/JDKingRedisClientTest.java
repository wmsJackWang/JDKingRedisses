package org.redisses.jdking.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JDKingRedisClientTest extends TestCase{

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public JDKingRedisClientTest( String testName )
    {
        super( testName ); 
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( JDKingRedisClientTest.class );
    }
    
    public void testJDKingRedisClient() {

    	JDKingRedisClient.doWithOut((jedis)->{
    		System.out.println(".............................测试JDKingRedisClient类doWithOut.............................");
    		jedis.set("JDKingRedisClient:StringSetKey", "hello world");
    		System.out.println("key JDKingRedisClient:StringSetKey的值:"+jedis.get("JDKingRedisClient:StringSetKey"));
    		
    	});
    	
    	JDKingResultInfo<String> result = JDKingRedisClient.domain((jedis)->{
    		
    		System.out.println(".............................测试JDKingRedisClient类doMain.............................");
    		jedis.set("JDKingRedisClient:StringSetKey", "hello world");
//    		if(true)
//    		throw new RuntimeException();
    		 
    		return jedis.get("JDKingRedisClient:StringSetKey");
    	});
    	
    	System.out.println("获取结果:"+result.getResultObj());
    	System.out.println("是否成功:"+result.isSuccess);
    	if(!result.isSuccess)
    	{
    		System.out.println("错误类型："+result.getErrMsg());
    		if(result.getException()!=null)
    			result.getException().printStackTrace();
    	}
    	
    }
    
     
}
