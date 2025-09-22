package com.jcondotta.account_recipients.infrastructure.config.aws.dynamodb;

import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientsTableProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class DynamoDbAccountRecipientTableConfig {

    @Bean
    public DynamoDbTable<AccountRecipientEntity> dynamoDbTable(
        DynamoDbEnhancedClient dynamoDbEnhancedClient,
        AccountRecipientsTableProperties tableProperties) {

        return dynamoDbEnhancedClient.table(tableProperties.tableName(), TableSchema.fromBean(AccountRecipientEntity.class));
    }
}