package com.jcondotta.account_recipients.get_recipients.usecase;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.model.PaginatedResult;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.account_recipients.application.usecase.get_recipients.mapper.GetAccountRecipientsQueryMapper;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.AccountRecipientDetails;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
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
 * Unit tests for {@link GetAccountRecipientsUseCaseImpl}.
 */
@ExtendWith(MockitoExtension.class)
class GetAccountRecipientsUseCaseImplTest {

  private static final QueryLimit QUERY_LIMIT_20 = QueryLimit.of(20);
  private static final PaginationCursor PAGINATION_CURSOR = PaginationCursor.of("encoded-cursor-123");

  @Mock
  private GetAccountRecipientsQueryMapper queryMapperMock;

  @Mock
  private GetAccountRecipientsRepository repositoryMock;

  @Mock
  private AccountRecipient accountRecipientMock1;

  @Mock
  private AccountRecipient accountRecipientMock2;

  @Mock
  private AccountRecipientDetails recipientDetails1;

  @Mock
  private AccountRecipientDetails recipientDetails2;

  @Mock
  private GetAccountRecipientsQuery queryMock;

  @Captor
  private ArgumentCaptor<GetAccountRecipientsQuery> queryCaptor;

  private GetAccountRecipientsUseCaseImpl useCase;
  private BankAccountId bankAccountId;
  private GetAccountRecipientsQueryParams queryParams;

  @BeforeEach
  void setUp() {
    useCase = new GetAccountRecipientsUseCaseImpl(queryMapperMock, repositoryMock);
    bankAccountId = BankAccountId.of(UUID.randomUUID());
    queryParams = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_20, null, PAGINATION_CURSOR);
  }

  @Test
  void shouldReturnMappedRecipientsAndNextCursor_andPutResultInCache_whenRepositoryReturnsResults() {
    var recipients = List.of(accountRecipientMock1, accountRecipientMock2);
    var paginatedResult = PaginatedResult.of(recipients, null);

    var query = GetAccountRecipientsQuery.of(bankAccountId, queryParams);

    when(repositoryMock.findByQuery(query)).thenReturn(paginatedResult);
    when(queryMapperMock.toAccountRecipient(accountRecipientMock1)).thenReturn(recipientDetails1);
    when(queryMapperMock.toAccountRecipient(accountRecipientMock2)).thenReturn(recipientDetails2);

    GetAccountRecipientsResult result = useCase.execute(query);

    verify(repositoryMock).findByQuery(query);
    verify(queryMapperMock).toAccountRecipient(accountRecipientMock1);
    verify(queryMapperMock).toAccountRecipient(accountRecipientMock2);

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
    PaginatedResult<AccountRecipient> paginatedResult = PaginatedResult.of(List.of(), null);
    var query = GetAccountRecipientsQuery.of(bankAccountId, queryParams);

    when(repositoryMock.findByQuery(query)).thenReturn(paginatedResult);

    GetAccountRecipientsResult result = useCase.execute(query);

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
    var query = GetAccountRecipientsQuery.of(bankAccountId, queryParams);

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
