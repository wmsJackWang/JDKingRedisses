package com.jsondream.redisses.Mq.pushPull;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jsondream.redisses.Mq.pushPull.bean.RedisQueueMessage;
import com.jsondream.redisses.Mq.pushPull.bean.User;
import com.jsondream.redisses.Mq.pushPull.constants.RedisMessageQueueConstants;
import com.jsondream.redisses.client.RedisClient;

/**
 * <p>
 *     消息doing过期或者未消费成功实现
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/18
 */
public class MessageRePending {
    // 默认最大限制是3s 
    private static final int maxTime= 1000 * 3;
    /**
     * 这里对外提供了重新把doing塞会到pending的
     * <br>这里考虑重新用delayedQueue实现
     */
    public static void rePending() {
    	
    	List<String> result = null;
        // TODO:扫描doing中过期的任务
    	Object resultObject = RedisClient.domain(jedis -> jedis.lrange(RedisMessageQueueConstants.consumerQueueName, -100, -1));
    	if(resultObject!=null) {
    		result = (List<String>)resultObject;
    		for(String userString:result)//遍历每一个数据，如果消息是个过期的消息，则进行相应处理并删除
    		{
		        // 获取这个元素的开始执行时间
    			User user =  JSON.parseObject(userString, User.class);
		        long execTime =user.getExecTime();
		        // 判断是否超时,根据执行时间加上最大时间值与现在时间节点进行对比
		        // 根据对比的结果判断,消息是否已经超时了,如果超时就会将超时消息进行重新发送消息和删除消息
		        if (execTime + maxTime < System.currentTimeMillis()) {
		            // TODO:重新塞会到pending队列中
		        	//如果是订单支付消息，根据过期消息数据对比数据库中是否已经存在完成订单，已有则直接删除，没有则重新提交的这种策略
		        	//这里看做消息没有执行，则将消息重新放入到pending队列中
		        	System.out.println("重新将消息："+userString+"放入到pending队列中去，并更新提交执行时间及从doing队列中删除");
		        	user.setExecTime(System.currentTimeMillis());
		        	user.setRuningTime(18);
		        	RedisQueueMessage<User> redisQueueMessage = new RedisQueueMessage<>(102,user);
		        	RedisClient.doWithOut(jedis->jedis.lrem(RedisMessageQueueConstants.consumerQueueName, -1, userString));
		        	RedisClient.doWithOut(jedis->jedis.lpush(RedisMessageQueueConstants.queueName, JSON.toJSONString(redisQueueMessage)));
		        }
    		}
    	}
    }
}
