package com.jcondotta.account_recipients.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisConnectionProperties(String host, int port, boolean ssl) { }
