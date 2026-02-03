package com.jcondotta.recipients.infrastructure.config.aws.dynamodb;

import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.properties.RecipientsTableProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class DynamoDbAccountRecipientTableConfig {

  @Bean
  public DynamoDbTable<RecipientEntity> dynamoDbTable(
      DynamoDbEnhancedClient dynamoDbEnhancedClient,
      RecipientsTableProperties tableProperties) {

    return dynamoDbEnhancedClient.table(
        tableProperties.tableName(), TableSchema.fromBean(RecipientEntity.class));
  }

  @Bean
  public DynamoDbIndex<RecipientEntity> recipientNameLSI(
      DynamoDbTable<RecipientEntity> accountRecipientsTable) {
    return accountRecipientsTable.index("RecipientNameLSI");
  }
}
