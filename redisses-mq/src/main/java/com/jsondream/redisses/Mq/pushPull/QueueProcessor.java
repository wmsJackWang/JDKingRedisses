package com.jsondream.redisses.Mq.pushPull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;

import com.google.common.util.concurrent.RateLimiter;
import com.jsondream.redisses.Mq.excuter.JackDKingExecutors;
import com.jsondream.redisses.Mq.pushPull.constants.RedisMessageQueueConstants;
import com.jsondream.redisses.client.RedisClient;

/**
 * 消息监听器。消费者端逻辑
 *
 * @author wangguangdong
 */
public class QueueProcessor {

    private int timeout = 0;//redis取出消息，队列消息不存在情况下，线程会阻塞30s。
    
    /**
     * 每秒钟只发出2个令牌，拿到令牌的请求才可以进入下一个业务
     */ 
    private RateLimiter seckillRateLimiter = RateLimiter.create(300);

    /**
     * 守护线程
     */
    private Thread daemonThread;

    //默认的执行线程池,默认线程池有30多个工作线程进行工作。
    private ExecutorService executor = JackDKingExecutors.newFixedThreadPool(2);
    
    private ScheduledExecutorService fixData = JackDKingExecutors.newScheduledThreadPool(1);

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @PostConstruct
    public void init() {
        daemonThread = new Thread(() -> process(executor));
        daemonThread.setDaemon(true);
        daemonThread.setName("Redis Queue Daemon Thread");
        daemonThread.start();
    }

    public void destroy() {
        // TODO
    }

    /**
     * Default constructor for convenient dependency injection via setters.
     */
    public QueueProcessor() {
        init();
    }

    /**
     * 异步执行。消息处理器在executor执行。
     *
     * @param executor
     * @throws Exception
     */
    public void process(ExecutorService executor) {
    	
    	long start = System.currentTimeMillis();
    	boolean isChange =false;
    	String message = null;
        while (true) {
        	//google的限流器
        	seckillRateLimiter.acquire();
            // 取出要消费的消息
        	
        	try {
        		message = RedisClient.domain(jedis -> jedis
                .brpoplpush(RedisMessageQueueConstants.queueName,
                    RedisMessageQueueConstants.consumerQueueName, timeout));
        	}catch (Exception e) {
				// TODO: handle exception
        		System.out.println("发生网络波动");
        		e.printStackTrace();
			}
            //            List<String> messages =
            //                RedisClient.domain(jedis ->jedis.brpop(this.timeout, queueName));
            // final String payload = messages.get(1);
            // final String payload = messages;

        	long end = System.currentTimeMillis();
        	
        	int time = (int) ((end-start)/1000);
        	
        	
        	
        	if(!isChange&&time == 15)
        	{
        		System.out.println("流量切换为 100/s");
        		isChange = true;
        		seckillRateLimiter = RateLimiter.create(100);
        	}
        	System.out.println("最新的消息为止，消耗的总时间："+time+"秒");
        	
        	if(message!=null)
        		submitTask(executor, message);
            //注意：提交完message后，把程序中的message清除，否则网络波动会重复提交这个消息。
            message = null;
        }
    }

    private void submitTask(ExecutorService executor, final String payload) {
        if (StringUtils.isNotBlank(payload)) {
            executor.execute(() -> MessageHandler.onMessage(payload));
            //Future future = executor.submit(() -> MessageHandler.onMessage(payload));
            //future.cancel(true);
        }
    }

    public static void main(String[] a) {
        QueueProcessor queueProcessor = new QueueProcessor();
        //补偿机制 
        queueProcessor.fixData.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				MessageRePending.rePending();
				
			}
		}, 0, 10, TimeUnit.SECONDS);
        
        for (; ; )
            ;
    }
    
    
}
