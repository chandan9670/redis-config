package com.tech.config;

import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import redis.clients.jedis.Jedis;

@Configuration
public class ConfigRedis {

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration connectionFactory = new RedisStandaloneConfiguration();
		connectionFactory.setHostName("localhost");
		connectionFactory.setPort(6379);
	    return new JedisConnectionFactory(connectionFactory);
	  }
	
	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate() {
	    final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
	    template.setConnectionFactory(jedisConnectionFactory());
	    template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
	    return template;
	}
	
	@Bean
	public Jedis jedis() {
		Jedis jedis = new Jedis("localhost", 6379);
		return jedis;
	}
	
	@Bean
	public ChannelTopic channelTopic() {
		return new ChannelTopic("pubSub");
	}
	
	@Bean
	public RedisMessageListenerContainer listenerContainer() {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(jedisConnectionFactory());
		container.setSubscriptionExecutor(Executors.newSingleThreadScheduledExecutor());
		container.setTaskExecutor(Executors.newSingleThreadScheduledExecutor());
		return container;
	}

	public ConfigRedis() {
		super();
		// TODO Auto-generated constructor stub
	}
}
