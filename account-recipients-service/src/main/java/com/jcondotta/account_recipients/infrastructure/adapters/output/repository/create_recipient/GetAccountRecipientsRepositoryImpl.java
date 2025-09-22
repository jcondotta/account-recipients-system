package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.create_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GetAccountRecipientsRepositoryImpl implements GetAccountRecipientsRepository {

    private final DynamoDbTable<AccountRecipientEntity> dynamoDbTable;
    private final AccountRecipientEntityMapper entityMapper;

    @Override
    public List<AccountRecipient> byBankAccountId(BankAccountId bankAccountId) {
        return dynamoDbTable.scan().items().stream()
            .map(entityMapper::toDomain)
            .toList();
    }
}