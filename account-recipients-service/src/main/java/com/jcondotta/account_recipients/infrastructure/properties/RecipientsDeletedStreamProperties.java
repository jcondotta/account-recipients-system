package com.jcondotta.account_recipients.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws.kinesis.streams.recipients-deleted")
public record RecipientsDeletedStreamProperties(String streamName) {}
