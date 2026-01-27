package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.create_recipient;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountRecipientRepositoryImplTest {

  @Mock
  private DynamoDbTable<AccountRecipientEntity> dynamoDbTable;

  @Mock
  private AccountRecipientEntityMapper entityMapper;

  @Mock
  private AccountRecipient accountRecipientMock;

  @Mock
  private AccountRecipientEntity accountRecipientEntityMock;

  @InjectMocks
  private CreateAccountRecipientRepositoryImpl repository;

  @Captor
  private ArgumentCaptor<AccountRecipientEntity> entityCaptor;

  @Test
  void shouldPutEntityIntoDynamoDb_whenAccountRecipientIsValid() {
    when(entityMapper.toEntity(accountRecipientMock)).thenReturn(accountRecipientEntityMock);

    repository.create(accountRecipientMock);

    verify(dynamoDbTable).putItem(entityCaptor.capture());
    assertThat(entityCaptor.getValue()).isSameAs(accountRecipientEntityMock);

    verify(entityMapper).toEntity(accountRecipientMock);
    verifyNoMoreInteractions(entityMapper, dynamoDbTable);
  }
}
