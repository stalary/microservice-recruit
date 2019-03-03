
package com.stalary.pf.push.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.stalary.pf.push.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * CacheConfig
 *
 * @author lirongqian
 * @since 2018/10/22
 */
@Configuration
@Slf4j
public class CacheConfig extends CachingConfigurerSupport {

    /**
     * redis序列化配置，使用lettuce客户端
     */
    @Bean
    public <T> RedisTemplate<String, T> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setKeySerializer(new PrefixRedisSerializer());
        template.setValueSerializer(new GenericFastJsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setKeySerializer(new PrefixRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("获取缓存时异常---key: " + key + " 异常信息:" + e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("handleCachePutError缓存时异常---key: " + key + " 异常信息:" + e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("handleCacheEvictError缓存时异常---key: " + key + " 异常信息:" + e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("清除缓存时异常--- 异常信息:" + e);
            }
        };
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 订阅频道
        container.addMessageListener(listenerAdapter, new PatternTopic(MessageService.MESSAGE_CHANNEL));
        container.addMessageListener(listenerAdapter, new PatternTopic(MessageService.CLOSE_CHANNEL));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(MessageService receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}