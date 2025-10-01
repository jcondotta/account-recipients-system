package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.delete_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.domain.recipient.exceptions.AccountRecipientNotFoundException;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeleteAccountRecipientRepositoryImpl implements DeleteAccountRecipientRepository {

    private final DynamoDbTable<AccountRecipientEntity> dynamoDbTable;

    @Override
    public void delete(BankAccountId bankAccountId, AccountRecipientId accountRecipientId) {
        var key = Key.builder()
            .partitionValue(AccountRecipientEntityKey.partitionKey(bankAccountId))
            .sortValue(AccountRecipientEntityKey.sortKey(accountRecipientId))
            .build();

        var deleteItemRequest = DeleteItemEnhancedRequest.builder()
            .key(key)
            .conditionExpression(Expression.builder()
                .expression("attribute_exists(partitionKey) AND attribute_exists(sortKey)").build())
            .build();

        try {
            dynamoDbTable.deleteItem(deleteItemRequest);
            log.info("Recipient deleted successfully [bankAccountId={}, accountRecipientId={}]", bankAccountId, accountRecipientId);
        }
        catch (ConditionalCheckFailedException e) {
            log.warn("Attempted to delete a non-existent recipient [bankAccountId={}, accountRecipientId={}]", bankAccountId, accountRecipientId);
            throw new AccountRecipientNotFoundException(bankAccountId, accountRecipientId);
        }
    }
}
