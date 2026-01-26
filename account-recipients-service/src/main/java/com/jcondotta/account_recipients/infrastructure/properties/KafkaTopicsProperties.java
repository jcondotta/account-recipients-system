package com.jcondotta.account_recipients.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicsProperties(String recipientCreated, String recipientDeleted) {
}