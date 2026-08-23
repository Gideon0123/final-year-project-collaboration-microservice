package com.example.COLLABORATION_SERVICE.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final GenericJackson2JsonRedisSerializer serializer;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(
                connectionFactory
        );

        StringRedisSerializer stringSerializer =
                new StringRedisSerializer();

        template.setKeySerializer(
                stringSerializer
        );

        template.setHashKeySerializer(
                stringSerializer
        );

        template.setValueSerializer(
                serializer
        );

        template.setHashValueSerializer(
                serializer
        );

        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisTemplate<String, String> presenceRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {

        RedisTemplate<String, String> template =
                new RedisTemplate<>();

        template.setConnectionFactory(
                connectionFactory
        );

        StringRedisSerializer stringSerializer =
                new StringRedisSerializer();

        template.setKeySerializer(
                stringSerializer
        );

        template.setHashKeySerializer(
                stringSerializer
        );

        template.setValueSerializer(
                stringSerializer
        );

        template.setHashValueSerializer(
                stringSerializer
        );

        template.afterPropertiesSet();

        return template;
    }
}