package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.delete_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.exceptions.AccountRecipientNotFoundException;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeleteAccountRecipientRepositoryImpl implements DeleteAccountRecipientRepository {

  private final DynamoDbTable<AccountRecipientEntity> dynamoDbTable;

  @Override
  @SuppressWarnings("java:Sxxxx") // AWS Enhanced Client already uses Consumer Builder idiomatically
  public void delete(AccountRecipient accountRecipient) {
    var condition =
        Expression.builder()
            .expression("attribute_exists(partitionKey) AND attribute_exists(sortKey)")
            .build();

    try {
      dynamoDbTable.deleteItem(b -> {
        b.key(k -> k
            .partitionValue(
                AccountRecipientEntityKey.partitionKey(accountRecipient.getBankAccountId())
            )
            .sortValue(
                AccountRecipientEntityKey.sortKey(accountRecipient.getRecipientId())
            )
        );
        b.conditionExpression(condition);
      });

      log.info(
          "Recipient deleted successfully [bankAccountId={}, recipientId={}]",
          accountRecipient.getBankAccountId(),
          accountRecipient.getRecipientId());

    } catch (ConditionalCheckFailedException e) {
      log.warn(
          "Attempted to delete a non-existent recipient [bankAccountId={}, recipientId={}]",
          accountRecipient.getBankAccountId(),
          accountRecipient.getRecipientId());

      throw new AccountRecipientNotFoundException(
          accountRecipient.getBankAccountId(),
          accountRecipient.getRecipientId(),
          e);
    }
  }
}
