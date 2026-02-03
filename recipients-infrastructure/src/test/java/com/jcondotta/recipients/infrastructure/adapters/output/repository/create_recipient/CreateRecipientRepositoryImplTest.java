package com.jcondotta.recipients.infrastructure.adapters.output.repository.create_recipient;

import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.mapper.RecipientEntityMapper;
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
class CreateRecipientRepositoryImplTest {

  @Mock
  private DynamoDbTable<RecipientEntity> dynamoDbTable;

  @Mock
  private RecipientEntityMapper entityMapper;

  @Mock
  private Recipient accountRecipientMock;

  @Mock
  private RecipientEntity recipientEntityMock;

  @InjectMocks
  private CreateRecipientRepositoryImpl repository;

  @Captor
  private ArgumentCaptor<RecipientEntity> entityCaptor;

  @Test
  void shouldPutEntityIntoDynamoDb_whenAccountRecipientIsValid() {
    when(entityMapper.toEntity(accountRecipientMock)).thenReturn(recipientEntityMock);

    repository.create(accountRecipientMock);

    verify(dynamoDbTable).putItem(entityCaptor.capture());
    assertThat(entityCaptor.getValue()).isSameAs(recipientEntityMock);

    verify(entityMapper).toEntity(accountRecipientMock);
    verifyNoMoreInteractions(entityMapper, dynamoDbTable);
  }
}
