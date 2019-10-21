package org.redisses.jdking.Lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MyTask implements Runnable{

    public static String key ="JACKDKING_DISTRIBUTED_KEY";
	 
	Long waitTime ;
	CountDownLatch countDownLatch;
	String taskName ;
	
	public MyTask(Long time,CountDownLatch countDownLatch,String name) {
		// TODO Auto-generated constructor stub 
		this.waitTime = time;
		this.countDownLatch = countDownLatch;
		this.taskName = name;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			//开启分布式锁获取线程
			countDownLatch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//默认过期时间为6s
		
		if(JackDKingRedisDistributedKey.lock(key, JackDKingRedisDistributedKey.defaultExpireTime, waitTime))
		{ 
			System.out.println(this.taskName+"	 成功获取锁");
			try {
				TimeUnit.MILLISECONDS.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JackDKingRedisDistributedKey.remove(key);
			System.out.println(this.taskName+"	 释放锁");
		}
		else
			System.out.println(this.taskName+"	获取锁失败");
		
		
	}
}
