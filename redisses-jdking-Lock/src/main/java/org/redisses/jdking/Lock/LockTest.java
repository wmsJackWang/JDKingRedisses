package org.redisses.jdking.Lock;

import java.util.concurrent.CountDownLatch;

public class LockTest {

	public static void main(String[] args) {
        long time = 6000;
		CountDownLatch countDownLatch =new CountDownLatch(1);
		
		for(int i =0 ;i<10;++i)
		{ 
			new Thread(new MyTask(time+i*1000, countDownLatch , ""+i)).start();
		}
		System.out.println("测试开始.......");
		countDownLatch.countDown();
	}
}
