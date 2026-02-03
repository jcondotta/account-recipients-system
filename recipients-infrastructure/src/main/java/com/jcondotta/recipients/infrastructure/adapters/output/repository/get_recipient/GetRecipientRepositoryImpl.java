package com.jcondotta.recipients.infrastructure.adapters.output.repository.get_recipient;

import com.jcondotta.recipients.application.ports.output.repository.get_recipient.GetRecipientRepository;
import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntityKey;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.mapper.RecipientEntityMapper;
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

  private final DynamoDbTable<RecipientEntity> dynamoDbTable;
  private final RecipientEntityMapper recipientEntityMapper;

  @Override
  public Optional<Recipient> getRecipient(BankAccountId bankAccountId, RecipientId recipientId) {
    log.debug("Fetching Recipient [bankAccountId={}, recipientId={}]", bankAccountId, recipientId);

    var key = Key.builder()
        .partitionValue(RecipientEntityKey.partitionKey(bankAccountId))
        .sortValue(RecipientEntityKey.sortKey(recipientId))
        .build();

    var accountRecipientEntity = dynamoDbTable.getItem(key);

    return Optional.ofNullable(accountRecipientEntity)
        .map(recipientEntityMapper::toDomain);
  }
}
