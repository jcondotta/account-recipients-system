package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipient.GetAccountRecipientRepository;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GetAccountRecipientRepositoryImpl implements GetAccountRecipientRepository {

  private final DynamoDbTable<AccountRecipientEntity> dynamoDbTable;
  private final AccountRecipientEntityMapper accountRecipientEntityMapper;

  @Override
  public Optional<AccountRecipient> getAccountRecipient(BankAccountId bankAccountId, RecipientId recipientId) {
    if (log.isDebugEnabled()) {
      log.debug("Fetching AccountRecipient [bankAccountId={}, recipientId={}]", bankAccountId, recipientId);
    }

    var key = Key.builder()
        .partitionValue(AccountRecipientEntityKey.partitionKey(bankAccountId))
        .sortValue(AccountRecipientEntityKey.sortKey(recipientId))
        .build();

    var accountRecipientEntity = dynamoDbTable.getItem(key);

    return Optional.ofNullable(accountRecipientEntity)
        .map(accountRecipientEntityMapper::toDomain);
  }
}
