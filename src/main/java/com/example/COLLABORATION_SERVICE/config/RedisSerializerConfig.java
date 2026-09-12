package com.example.COLLABORATION_SERVICE.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisSerializerConfig {

    @Bean
    public GenericJackson2JsonRedisSerializer redisSerializer() {

        BasicPolymorphicTypeValidator typeValidator =
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.example.COLLABORATION_SERVICE")
                        .allowIfSubType("java.util")
                        .build();

        ObjectMapper mapper = JsonMapper.builder()
                .polymorphicTypeValidator(typeValidator)
                .addModule(new JavaTimeModule())
                .build();

        mapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        return GenericJackson2JsonRedisSerializer.builder()
                .objectMapper(mapper)
                .defaultTyping(true)
                .typeHintPropertyName("@class")
                .build();
    }
}