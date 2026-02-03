package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipient;

import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.domain.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRecipientRepositoryImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  @Mock
  private DynamoDbTable<AccountRecipientEntity> dynamoDbTable;
  @Mock
  private AccountRecipientEntityMapper accountRecipientEntityMapper;
  @Mock
  private AccountRecipientEntity accountRecipientEntityMock;
  @Mock
  private Recipient accountRecipientMock;
  @InjectMocks
  private GetRecipientRepositoryImpl repository;

  @Test
  void shouldReturnAccountRecipient_whenEntityExists() {
    Key key = Key.builder()
        .partitionValue(AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID))
        .sortValue(AccountRecipientEntityKey.sortKey(RECIPIENT_ID))
        .build();

    when(dynamoDbTable.getItem(key)).thenReturn(accountRecipientEntityMock);
    when(accountRecipientEntityMapper.toDomain(accountRecipientEntityMock)).thenReturn(accountRecipientMock);

    assertThat(repository.getRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .hasValue(accountRecipientMock);

    verify(dynamoDbTable).getItem(key);
    verify(accountRecipientEntityMapper).toDomain(accountRecipientEntityMock);

    verifyNoMoreInteractions(dynamoDbTable, accountRecipientEntityMapper);
  }

  @Test
  void shouldReturnEmptyOptional_whenEntityDoesNotExist() {
    Key key = Key.builder()
        .partitionValue(AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID))
        .sortValue(AccountRecipientEntityKey.sortKey(RECIPIENT_ID))
        .build();

    when(dynamoDbTable.getItem(key)).thenReturn(null);

    assertThat(repository.getRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .isEmpty();

    verify(dynamoDbTable).getItem(key);
    verifyNoInteractions(accountRecipientEntityMapper);
  }
}
