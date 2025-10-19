package com.jcondotta.account_recipients.get_recipients.usecase;

import com.jcondotta.account_recipients.application.ports.output.cache.AccountRecipientsQueryCacheKey;
import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginatedResult;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.QueryLimit;
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
import wiremock.com.github.jknack.handlebars.internal.Throwing;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    private CacheStore<GetAccountRecipientsResult> cacheStoreMock;

    @Mock
    private AccountRecipient accountRecipientMock1;

    @Mock
    private AccountRecipient accountRecipientMock2;

    @Mock
    private AccountRecipientDetails recipientDetails1;

    @Mock
    private AccountRecipientDetails recipientDetails2;

    @Captor
    private ArgumentCaptor<GetAccountRecipientsQuery> queryCaptor;

    private GetAccountRecipientsUseCaseImpl useCase;
    private BankAccountId bankAccountId;
    private GetAccountRecipientsQueryParams queryParams;

    @BeforeEach
    void setUp() {
        useCase = new GetAccountRecipientsUseCaseImpl(queryMapperMock, cacheStoreMock, repositoryMock);
        bankAccountId = BankAccountId.of(UUID.randomUUID());
        queryParams = GetAccountRecipientsQueryParams.of(QUERY_LIMIT_20, PAGINATION_CURSOR);
    }

    @Test
    void shouldReturnMappedRecipientsAndNextCursor_whenRepositoryReturnsResults() {
        var recipients = List.of(accountRecipientMock1, accountRecipientMock2);
        var paginatedResult = PaginatedResult.of(recipients, null);

        var query = GetAccountRecipientsQuery.of(bankAccountId, queryParams);

        when(repositoryMock.findByQuery(query)).thenReturn(paginatedResult);
        when(queryMapperMock.toAccountRecipient(accountRecipientMock1)).thenReturn(recipientDetails1);
        when(queryMapperMock.toAccountRecipient(accountRecipientMock2)).thenReturn(recipientDetails2);
        when(cacheStoreMock.getIfPresent(anyString())).thenReturn(Optional.empty());

        GetAccountRecipientsResult result = useCase.execute(query);

        verify(queryMapperMock).toAccountRecipient(accountRecipientMock1);
        verify(queryMapperMock).toAccountRecipient(accountRecipientMock2);
        verify(cacheStoreMock).getIfPresent(anyString());
        verify(cacheStoreMock).put(anyString(), eq(result));

        verify(repositoryMock).findByQuery(queryCaptor.capture());
        assertThat(queryCaptor.getValue()).isEqualTo(query);

        verifyNoMoreInteractions(repositoryMock, cacheStoreMock, queryMapperMock);

        assertThat(result)
            .satisfies(it -> {
                assertThat(it.accountRecipients()).containsExactly(recipientDetails1, recipientDetails2);
                assertThat(it.nextCursor()).isNull();
            });
    }

    @Test
    void shouldReturnCachedResult_whenCacheContainsMatchedCacheKey() {
        var query = GetAccountRecipientsQuery.of(bankAccountId, queryParams);

        var cachedRecipientDetailsList = List.of(recipientDetails1, recipientDetails2);
        var cachedAccountRecipientsResult = GetAccountRecipientsResult.of(cachedRecipientDetailsList, null);

        when(cacheStoreMock.getIfPresent(anyString())).thenReturn(Optional.of(cachedAccountRecipientsResult));

        GetAccountRecipientsResult result = useCase.execute(query);

        verify(cacheStoreMock).getIfPresent(anyString());
        verify(cacheStoreMock, never()).put(anyString(), any(GetAccountRecipientsResult.class));

        verifyNoInteractions(repositoryMock, queryMapperMock);

        verifyNoMoreInteractions(repositoryMock, cacheStoreMock, queryMapperMock);

        assertThat(result)
            .satisfies(it -> {
                assertThat(it.accountRecipients()).containsExactly(recipientDetails1, recipientDetails2);
                assertThat(it.nextCursor()).isNull();
            });
    }

//    @Test
//    void shouldReturnEmptyListAndNullCursor_whenRepositoryReturnsEmptyResults() {
//        // given
//        var paginatedResult = new PaginatedResult<AccountRecipient>(List.of(), null);
//        when(repositoryMock.findByQuery(queryMock)).thenReturn(paginatedResult);
//
//        // when
//        GetAccountRecipientsResult result = useCase.execute(queryMock);
//
//        // then
//        verify(repositoryMock).findByQuery(queryMock);
//        verifyNoInteractions(queryMapperMock);
//
//        assertThat(result)
//            .satisfies(it -> {
//                assertThat(it.accountRecipients()).isEmpty();
//                assertThat(it.nextCursor()).isNull();
//            });
//    }
//
//    @Test
//    void shouldPropagateException_whenRepositoryThrowsUnexpectedError() {
//        when(repositoryMock.findByQuery(queryMock)).thenThrow(new RuntimeException("database error"));
//
//        try {
//            useCase.execute(queryMock);
//        } catch (RuntimeException ex) {
//            assertThat(ex)
//                .isInstanceOf(RuntimeException.class)
//                .hasMessage("database error");
//        }
//
//        verify(repositoryMock).findByQuery(queryMock);
//        verifyNoInteractions(queryMapperMock);
//    }
}
