package com.jcondotta.recipients.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws.dynamodb.tables.recipients")
public record RecipientsTableProperties(String tableName) {
}
