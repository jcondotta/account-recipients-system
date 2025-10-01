package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.create_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.create_recipient.CreateAccountRecipientRepository;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CreateAccountRecipientRepositoryImpl implements CreateAccountRecipientRepository {

    private final DynamoDbTable<AccountRecipientEntity> dynamoDbTable;
    private final AccountRecipientEntityMapper entityMapper;

    @Override
    public void create(AccountRecipient accountRecipient) {
        dynamoDbTable.putItem(entityMapper.toEntity(accountRecipient));
    }
}
