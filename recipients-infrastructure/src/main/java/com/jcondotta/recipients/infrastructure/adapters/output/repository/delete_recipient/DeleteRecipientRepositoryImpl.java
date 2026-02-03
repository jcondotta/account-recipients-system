package com.jcondotta.recipients.infrastructure.adapters.output.repository.delete_recipient;

import com.jcondotta.recipients.application.ports.output.repository.delete_recipient.DeleteRecipientRepository;
import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.exceptions.RecipientNotFoundException;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntityKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeleteRecipientRepositoryImpl implements DeleteRecipientRepository {

  private final DynamoDbTable<RecipientEntity> dynamoDbTable;

  @Override
  public void delete(Recipient recipient) {
    var condition =
        Expression.builder()
            .expression("attribute_exists(partitionKey) AND attribute_exists(sortKey)")
            .build();

    try {
      dynamoDbTable.deleteItem(b -> {
        b.key(k -> k
            .partitionValue(
                RecipientEntityKey.partitionKey(recipient.getBankAccountId())
            )
            .sortValue(
                RecipientEntityKey.sortKey(recipient.getRecipientId())
            )
        );
        b.conditionExpression(condition);
      });

      log.info(
          "Recipient deleted successfully [bankAccountId={}, recipientId={}]",
          recipient.getBankAccountId(),
          recipient.getRecipientId());

    } catch (ConditionalCheckFailedException e) {
      log.warn(
          "Attempted to delete a non-existent recipient [bankAccountId={}, recipientId={}]",
          recipient.getBankAccountId(),
          recipient.getRecipientId());

      throw new RecipientNotFoundException(
          recipient.getBankAccountId(),
          recipient.getRecipientId(),
          e);
    }
  }
}
