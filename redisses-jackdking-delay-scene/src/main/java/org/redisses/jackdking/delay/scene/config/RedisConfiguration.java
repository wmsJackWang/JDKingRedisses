package org.redisses.jackdking.delay.scene.config;

import org.redisses.jackdking.delay.scene.listener.JackDKingKeyExpiredListener;
import org.redisses.jackdking.delay.scene.listener.JackDKingKeyExpiredListenerV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfiguration {
    @Autowired 
    private RedisConnectionFactory redisConnectionFactory;
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }
    
    @Bean
    public JackDKingKeyExpiredListener keyExpiredListener() {
        return new JackDKingKeyExpiredListener(this.redisMessageListenerContainer());
    }
    
    /*
     * 验证了，多个listener都会监听到key过期事件，且都会处理。
     */
    @Bean
    @Qualifier("JackDKingKeyExpiredListenerV1")
    public JackDKingKeyExpiredListenerV1 JackDKingKeyExpiredListenerV1() {
        return new JackDKingKeyExpiredListenerV1(this.redisMessageListenerContainer());
    }
    
}