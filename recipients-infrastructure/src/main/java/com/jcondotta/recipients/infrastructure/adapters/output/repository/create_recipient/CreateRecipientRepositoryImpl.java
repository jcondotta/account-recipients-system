package com.jcondotta.recipients.infrastructure.adapters.output.repository.create_recipient;

import com.jcondotta.recipients.application.ports.output.repository.create_recipient.CreateRecipientRepository;
import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.mapper.RecipientEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CreateRecipientRepositoryImpl implements CreateRecipientRepository {

  private final DynamoDbTable<RecipientEntity> dynamoDbTable;
  private final RecipientEntityMapper entityMapper;

  @Override
  public Recipient create(Recipient recipient) {
    dynamoDbTable.putItem(entityMapper.toEntity(recipient));

    return recipient;
  }
}
