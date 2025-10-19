package com.jcondotta.account_recipients.infrastructure.config.redis;

import com.jcondotta.account_recipients.infrastructure.properties.RedisConnectionProperties;
import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Slf4j
@Configuration
public class RedisConnectionConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisConnectionProperties properties) {
        log.atInfo()
            .setMessage("Redis initialized with port={} ssl={}")
            .addArgument(properties.port())
            .addArgument(properties.ssl())
            .log();

        var clientBuilder = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(5))
            .shutdownTimeout(Duration.ofMillis(100))
            .clientResources(ClientResources.create());

        if(properties.ssl()){
            clientBuilder.useSsl();
        }

        var redisStandaloneConfiguration = new RedisStandaloneConfiguration(properties.host(), properties.port());
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientBuilder.build());
    }
}
