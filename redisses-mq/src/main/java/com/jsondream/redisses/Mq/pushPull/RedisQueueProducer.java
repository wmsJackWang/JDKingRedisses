package com.jsondream.redisses.Mq.pushPull;

import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.jsondream.redisses.Mq.pushPull.bean.RedisQueueMessage;
import com.jsondream.redisses.Mq.pushPull.bean.User;
import com.jsondream.redisses.Mq.pushPull.constants.RedisMessageQueueConstants;
import com.jsondream.redisses.client.RedisClient;

/**
 * <p>
 *     生产者发送消息模块
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/12
 */
public class RedisQueueProducer {

    private long queueSize = 1000;

    public boolean pushToQueue(RedisQueueMessage registrationInfo) {
        try {
            final String jsonPayload = JSON.toJSONString(registrationInfo);
            RedisClient.doWithOut(jedis -> {
                Long result = jedis.lpush(
                    RedisMessageQueueConstants.queueName, jsonPayload);
                // 为了避免队列内存溢出，只保留1000个消息
                // jedis.ltrim(QUEUE_MESSAGE_SUBSCRIBE, -queueSize, -1);
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @param registrationInfo
     * @return
     */
    public boolean pushToQueueV2(Object registrationInfo) {
        try {
            final String messageBody = JSON.toJSONString(registrationInfo);
            RedisClient.doWithOut(jedis -> {
                Long result = jedis.lpush(RedisMessageQueueConstants.queueName, messageBody);
                // 为了避免队列内存溢出，只保留1000个消息
                // jedis.ltrim(QUEUE_MESSAGE_SUBSCRIBE, -queueSize, -1);
            });
            return true;
        } catch (Exception e) { 
            return false; 
        }
    }


    public static void main(String[]a){
        int id =1000;
        RedisQueueProducer redisQueueProducer = new RedisQueueProducer();
        long start = System.currentTimeMillis();
        for(int i =0;i<3000;i++){
            User user = new User();
            user.setId(String.valueOf(id + i));
            user.setUserName("jack");
            user.setPwd("root");
            user.setExecTime(System.currentTimeMillis());
            //下面一段代码模拟 每50个消息中就有一个导致消耗线程 消费消息失败
            Random random = new Random();
            int randomValue = random.nextInt(50);
            user.setRuningTime(randomValue<10?10000:randomValue);
            
            RedisQueueMessage<User> redisQueueMessage = new RedisQueueMessage<>(102,user);
            //        if(redisQueueProducer.pushToQueue(redisQueueMessage)){
            //            System.out.println("success");
            //        }
            redisQueueProducer.pushToQueueV2(redisQueueMessage);
        }
        System.out.println("总共花费的时间是"+(System.currentTimeMillis()-start));
    }
}
