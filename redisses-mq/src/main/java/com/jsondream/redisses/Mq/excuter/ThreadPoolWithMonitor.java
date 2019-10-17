package com.jsondream.redisses.Mq.excuter;


import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ThreadPoolWithMonitor extends ThreadPoolExecutor{

	Logger logger = Logger.getLogger(ThreadPoolWithMonitor.class.getName());
	private String poolname = new String("defaultThreadPool") ;
	
	public ThreadPoolWithMonitor(int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
		
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
}

public ThreadPoolWithMonitor(int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory) {
	
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,threadFactory);
}

public ThreadPoolWithMonitor(int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler) {
	
		this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,Executors.defaultThreadFactory(), handler);
}

public ThreadPoolWithMonitor(int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
	
		super(corePoolSize ,maximumPoolSize,keepAliveTime,unit,workQueue,threadFactory,handler);
}



/*
 * 	每次任务执行开始和结束前后都会调用的方法，在这个线程池监控修饰类override
 */
	ConcurrentHashMap<String, Date> timeset = new ConcurrentHashMap<String, Date>();



	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		// TODO Auto-generated method stub
		super.beforeExecute(t, r);
		timeset.put(String.valueOf(r.hashCode()), new Date());
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		// TODO Auto-generated method stub
		super.afterExecute(r, t);
		Date start = timeset.get(String.valueOf(r.hashCode()));
		timeset.remove(String.valueOf(r.hashCode()));
		Long diff  = new Date().getTime() - start.getTime();
//		logger.info(r.hashCode()+"任务消耗时间:"+String.valueOf(diff));
		// 统计任务耗时、初始线程数、核心线程数、正在执行的任务数量、已完成任务数量、任务总数、队列里缓存的任务数量、池中存在的最大线程数、最大允许的线程数、线程空闲时间、线程池是否关闭、线程池是否终止
        logger.info(String.format(this.poolname
                        + "-pool-monitor: Duration: %d ms, PoolSize: %d, CorePoolSize: %d, Active: %d, Completed: %d, Task: %d, Queue: %d, LargestPoolSize: %d, MaximumPoolSize: %d,  KeepAliveTime: %d, isShutdown: %s, isTerminated: %s",
                diff, this.getPoolSize(), this.getCorePoolSize(), this.getActiveCount(), this.getCompletedTaskCount(), this.getTaskCount(),
                this.getQueue().size(), this.getLargestPoolSize(), this.getMaximumPoolSize(), this.getKeepAliveTime(TimeUnit.MILLISECONDS),
                this.isShutdown(), this.isTerminated()));
        System.out.println("错误描述 :"+t.getMessage());
	}

	@Override
	protected void terminated() {
		// TODO Auto-generated method stub
		super.terminated();
		System.out.println("##################################### who terminated");
	}


}
