package com.jcondotta.recipients.infrastructure.adapters.output.repository.get_recipient;

import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntityKey;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.mapper.RecipientEntityMapper;
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
  private DynamoDbTable<RecipientEntity> dynamoDbTable;
  @Mock
  private RecipientEntityMapper recipientEntityMapper;
  @Mock
  private RecipientEntity recipientEntityMock;
  @Mock
  private Recipient recipientMock;
  @InjectMocks
  private GetRecipientRepositoryImpl repository;

  @Test
  void shouldReturnRecipient_whenEntityExists() {
    Key key = Key.builder()
        .partitionValue(RecipientEntityKey.partitionKey(BANK_ACCOUNT_ID))
        .sortValue(RecipientEntityKey.sortKey(RECIPIENT_ID))
        .build();

    when(dynamoDbTable.getItem(key)).thenReturn(recipientEntityMock);
    when(recipientEntityMapper.toDomain(recipientEntityMock)).thenReturn(recipientMock);

    assertThat(repository.getRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .hasValue(recipientMock);

    verify(dynamoDbTable).getItem(key);
    verify(recipientEntityMapper).toDomain(recipientEntityMock);

    verifyNoMoreInteractions(dynamoDbTable, recipientEntityMapper);
  }

  @Test
  void shouldReturnEmptyOptional_whenEntityDoesNotExist() {
    Key key = Key.builder()
        .partitionValue(RecipientEntityKey.partitionKey(BANK_ACCOUNT_ID))
        .sortValue(RecipientEntityKey.sortKey(RECIPIENT_ID))
        .build();

    when(dynamoDbTable.getItem(key)).thenReturn(null);

    assertThat(repository.getRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .isEmpty();

    verify(dynamoDbTable).getItem(key);
    verifyNoInteractions(recipientEntityMapper);
  }
}
