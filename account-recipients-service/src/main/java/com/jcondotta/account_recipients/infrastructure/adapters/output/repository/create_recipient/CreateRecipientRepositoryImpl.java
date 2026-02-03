package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.create_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.create_recipient.CreateRecipientRepository;
import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CreateRecipientRepositoryImpl implements CreateRecipientRepository {

  private final DynamoDbTable<AccountRecipientEntity> dynamoDbTable;
  private final AccountRecipientEntityMapper entityMapper;

  @Override
  public Recipient create(Recipient recipient) {
    dynamoDbTable.putItem(entityMapper.toEntity(recipient));

    return recipient;
  }
}
