package org.redisses.jackdking.delay.scene;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import redis.clients.jedis.Jedis;

/**
 * Hello world!
 * 此demo，可以用在支付订单过期，优惠券过期场景。基于redis实现是个速度非常快的解决方案
 * 
 * 问题： 如果监听器执行失败，或者得到消息的监听器都宕机了，则过期消息事件就会丢失。因此对过期消息事件非常重要的场景，还是使用延迟mq。
 *		
 */
@SpringBootApplication
public class DelaySceneApplication  implements ApplicationRunner
{
    public static void main( String[] args )
    {
    	SpringApplication.run(DelaySceneApplication.class,args);
    }

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
		
		Jedis jedis = new Jedis("localhost");//连接localhost地址的redis，默认端口6379
		jedis.flushAll();
		
        // 放入redis，并设置过期时间为5秒
		jedis.set("test", "test", "NX", "EX", 5);
		
	}
}