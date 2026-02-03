package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.delete_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteRecipientRepository;
import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.domain.exceptions.RecipientNotFoundException;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
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

  private final DynamoDbTable<AccountRecipientEntity> dynamoDbTable;

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
                AccountRecipientEntityKey.partitionKey(recipient.getBankAccountId())
            )
            .sortValue(
                AccountRecipientEntityKey.sortKey(recipient.getRecipientId())
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
