package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipient.GetRecipientRepository;
import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GetRecipientRepositoryImpl implements GetRecipientRepository {

  private final DynamoDbTable<AccountRecipientEntity> dynamoDbTable;
  private final AccountRecipientEntityMapper accountRecipientEntityMapper;

  @Override
  public Optional<Recipient> getRecipient(BankAccountId bankAccountId, RecipientId recipientId) {
    log.debug("Fetching Recipient [bankAccountId={}, recipientId={}]", bankAccountId, recipientId);

    var key = Key.builder()
        .partitionValue(AccountRecipientEntityKey.partitionKey(bankAccountId))
        .sortValue(AccountRecipientEntityKey.sortKey(recipientId))
        .build();

    var accountRecipientEntity = dynamoDbTable.getItem(key);

    return Optional.ofNullable(accountRecipientEntity)
        .map(accountRecipientEntityMapper::toDomain);
  }
}
