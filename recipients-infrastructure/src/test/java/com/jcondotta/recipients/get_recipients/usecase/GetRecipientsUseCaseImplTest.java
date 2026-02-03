package com.jcondotta.recipients.get_recipients.usecase;

import com.jcondotta.recipients.application.ports.output.repository.get_recipients.GetRecipientsRepository;
import com.jcondotta.recipients.application.ports.output.repository.get_recipients.model.GetRecipientsQueryParams;
import com.jcondotta.recipients.application.ports.output.repository.shared.model.PaginatedResult;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.recipients.application.usecase.get_recipients.mapper.GetRecipientsQueryMapper;
import com.jcondotta.recipients.application.usecase.get_recipients.model.RecipientDetails;
import com.jcondotta.recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.recipients.application.usecase.get_recipients.model.result.GetRecipientsResult;
import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GetRecipientsUseCaseImpl}.
 */
@ExtendWith(MockitoExtension.class)
class GetRecipientsUseCaseImplTest {

  private static final QueryLimit QUERY_LIMIT_20 = QueryLimit.of(20);
  private static final PaginationCursor PAGINATION_CURSOR = PaginationCursor.of("encoded-cursor-123");

  @Mock
  private GetRecipientsQueryMapper queryMapperMock;

  @Mock
  private GetRecipientsRepository repositoryMock;

  @Mock
  private Recipient accountRecipientMock1;

  @Mock
  private Recipient accountRecipientMock2;

  @Mock
  private RecipientDetails recipientDetails1;

  @Mock
  private RecipientDetails recipientDetails2;

  @Mock
  private GetRecipientsQuery queryMock;

  @Captor
  private ArgumentCaptor<GetRecipientsQuery> queryCaptor;

  private GetRecipientsUseCaseImpl useCase;
  private BankAccountId bankAccountId;
  private GetRecipientsQueryParams queryParams;

  @BeforeEach
  void setUp() {
    useCase = new GetRecipientsUseCaseImpl(queryMapperMock, repositoryMock);
    bankAccountId = BankAccountId.of(UUID.randomUUID());
    queryParams = GetRecipientsQueryParams.of(QUERY_LIMIT_20, null, PAGINATION_CURSOR);
  }

  @Test
  void shouldReturnMappedRecipientsAndNextCursor_andPutResultInCache_whenRepositoryReturnsResults() {
    var recipients = List.of(accountRecipientMock1, accountRecipientMock2);
    var paginatedResult = PaginatedResult.of(recipients, null);

    var query = GetRecipientsQuery.of(bankAccountId, queryParams);

    when(repositoryMock.findByQuery(query)).thenReturn(paginatedResult);
    when(queryMapperMock.toRecipient(accountRecipientMock1)).thenReturn(recipientDetails1);
    when(queryMapperMock.toRecipient(accountRecipientMock2)).thenReturn(recipientDetails2);

    GetRecipientsResult result = useCase.execute(query);

    verify(repositoryMock).findByQuery(query);
    verify(queryMapperMock).toRecipient(accountRecipientMock1);
    verify(queryMapperMock).toRecipient(accountRecipientMock2);

    verifyNoMoreInteractions(repositoryMock, queryMapperMock);

    assertThat(result)
        .satisfies(it -> {
          assertThat(it.accountRecipients())
              .containsExactly(recipientDetails1, recipientDetails2);
          assertThat(it.nextCursor()).isNull();
        });
  }

  @Test
  void shouldReturnEmptyListAndNullCursor_andPutInCache_whenRepositoryReturnsEmptyResults() {
    PaginatedResult<Recipient> paginatedResult = PaginatedResult.of(List.of(), null);
    var query = GetRecipientsQuery.of(bankAccountId, queryParams);

    when(repositoryMock.findByQuery(query)).thenReturn(paginatedResult);

    GetRecipientsResult result = useCase.execute(query);

    verify(repositoryMock).findByQuery(query);
    verifyNoInteractions(queryMapperMock);

    verifyNoMoreInteractions(repositoryMock, queryMapperMock);

    assertThat(result)
        .satisfies(it -> {
          assertThat(it.accountRecipients()).isEmpty();
          assertThat(it.nextCursor()).isNull();
        });
  }

  @Test
  void shouldPropagateException_whenRepositoryThrowsUnexpectedError() {
    var query = GetRecipientsQuery.of(bankAccountId, queryParams);

    when(repositoryMock.findByQuery(query))
        .thenThrow(new RuntimeException("database error"));

    assertThatThrownBy(() -> useCase.execute(query))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("database error");

    verify(repositoryMock).findByQuery(query);
    verifyNoInteractions(queryMapperMock);

    verifyNoMoreInteractions(repositoryMock);
  }
}
