package com.example.pioneerpixel.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    poolConfig.setMaxTotal(50);
    poolConfig.setMaxIdle(10);
    poolConfig.setMinIdle(5);

    LettucePoolingClientConfiguration poolClientConfig =
        LettucePoolingClientConfiguration.builder().poolConfig(poolConfig).build();

    RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
    redisConfig.setHostName("localhost");
    redisConfig.setPort(6379);
    redisConfig.setPassword(RedisPassword.of("test"));
    redisConfig.setDatabase(0);

    return new LettuceConnectionFactory(redisConfig, poolClientConfig);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      LettuceConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());

    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public RedisCacheManager cacheManager(LettuceConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration defaultCacheConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()))
            .prefixCacheNameWith("pioneerpixel:");

    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    cacheConfigurations.put("users", defaultCacheConfig.entryTtl(Duration.ofMinutes(10)));
    cacheConfigurations.put("userContacts", defaultCacheConfig.entryTtl(Duration.ofHours(1)));

    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaultCacheConfig)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
