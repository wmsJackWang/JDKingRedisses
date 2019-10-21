package org.redisses.jdking.Lock;

import java.util.concurrent.CountDownLatch;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class JackDKingRedisDistributedKeyTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public JackDKingRedisDistributedKeyTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( JackDKingRedisDistributedKeyTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );

        long time = 6000;
		CountDownLatch countDownLatch =new CountDownLatch(1);
		
		for(int i =0 ;i<10;++i)
		{
			System.out.println("创建任务");
			new Thread(new MyTask(time+i*10000, countDownLatch , ""+i)).start();
		}
		System.out.println("测试开始.......");
		countDownLatch.countDown();
    }
}
