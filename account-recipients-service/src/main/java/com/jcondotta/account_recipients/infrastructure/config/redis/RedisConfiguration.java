package com.jcondotta.account_recipients.infrastructure.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.infrastructure.adapters.output.cache.RedisCacheStore;
import com.jcondotta.account_recipients.infrastructure.adapters.output.metrics.CacheMetricsRecorder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
public class RedisConfiguration {

    @Bean
    public CacheStore<GetAccountRecipientsResult> getAccountRecipientsResultCacheStore(
        RedisConnectionFactory connectionFactory,
        ObjectMapper objectMapper,
        CacheMetricsRecorder metricsRecorder
    ) {
        RedisTemplate<String, GetAccountRecipientsResult> template =
            createTypedRedisTemplate(connectionFactory, objectMapper, GetAccountRecipientsResult.class);

        return new RedisCacheStore<>(
            template,
            Duration.ofHours(24),
            GetAccountRecipientsResult.class,
            metricsRecorder
        );
    }

    /**
     * Factory method to build a type-safe RedisTemplate using a
     * Jackson2JsonRedisSerializer for the given type.
     */
    private <T> RedisTemplate<String, T> createTypedRedisTemplate(
        RedisConnectionFactory connectionFactory,
        ObjectMapper objectMapper,
        Class<T> valueClass
    ) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        var keySerializer = new StringRedisSerializer();
        var valueSerializer = new Jackson2JsonRedisSerializer<>(valueClass);
        valueSerializer.setObjectMapper(objectMapper);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
