package com.jcondotta.account_recipients.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "cloud.aws.dynamodb.tables.account-recipients")
public record AccountRecipientsTableProperties(String tableName) {}
