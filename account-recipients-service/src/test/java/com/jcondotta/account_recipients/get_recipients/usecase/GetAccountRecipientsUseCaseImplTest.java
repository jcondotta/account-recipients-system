package com.jcondotta.account_recipients.get_recipients.usecase;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginatedResult;
import com.jcondotta.account_recipients.application.usecase.get_recipients.mapper.GetAccountRecipientsQueryMapper;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.AccountRecipientDetails;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GetAccountRecipientsUseCaseImpl}.
 */
@ExtendWith(MockitoExtension.class)
class GetAccountRecipientsUseCaseImplTest {

    private static final String NEXT_CURSOR = "cursor-xyz";

    @Mock
    private GetAccountRecipientsQueryMapper queryMapperMock;

    @Mock
    private GetAccountRecipientsRepository repositoryMock;

    @Mock
    private GetAccountRecipientsQuery queryMock;

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

    @BeforeEach
    void setUp() {
        useCase = new GetAccountRecipientsUseCaseImpl(queryMapperMock, repositoryMock);
    }

    @Test
    void shouldReturnMappedRecipientsAndNextCursor_whenRepositoryReturnsResults() {
        var recipients = List.of(accountRecipientMock1, accountRecipientMock2);
        var paginatedResult = new PaginatedResult<>(recipients, NEXT_CURSOR);

        when(repositoryMock.findByQuery(queryMock)).thenReturn(paginatedResult);
        when(queryMapperMock.toAccountRecipient(accountRecipientMock1)).thenReturn(recipientDetails1);
        when(queryMapperMock.toAccountRecipient(accountRecipientMock2)).thenReturn(recipientDetails2);

        GetAccountRecipientsResult result = useCase.execute(queryMock);

        verify(repositoryMock).findByQuery(queryCaptor.capture());
        verify(queryMapperMock).toAccountRecipient(accountRecipientMock1);
        verify(queryMapperMock).toAccountRecipient(accountRecipientMock2);
        verifyNoMoreInteractions(repositoryMock, queryMapperMock);

        assertThat(queryCaptor.getValue()).isEqualTo(queryMock);

        assertThat(result)
            .satisfies(it -> {
                assertThat(it.accountRecipients()).containsExactly(recipientDetails1, recipientDetails2);
                assertThat(it.nextCursor()).isEqualTo(NEXT_CURSOR);
            });
    }

    @Test
    void shouldReturnEmptyListAndNullCursor_whenRepositoryReturnsEmptyResults() {
        // given
        var paginatedResult = new PaginatedResult<AccountRecipient>(List.of(), null);
        when(repositoryMock.findByQuery(queryMock)).thenReturn(paginatedResult);

        // when
        GetAccountRecipientsResult result = useCase.execute(queryMock);

        // then
        verify(repositoryMock).findByQuery(queryMock);
        verifyNoInteractions(queryMapperMock);

        assertThat(result)
            .satisfies(it -> {
                assertThat(it.accountRecipients()).isEmpty();
                assertThat(it.nextCursor()).isNull();
            });
    }

    @Test
    void shouldPropagateException_whenRepositoryThrowsUnexpectedError() {
        when(repositoryMock.findByQuery(queryMock)).thenThrow(new RuntimeException("database error"));

        try {
            useCase.execute(queryMock);
        } catch (RuntimeException ex) {
            assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("database error");
        }

        verify(repositoryMock).findByQuery(queryMock);
        verifyNoInteractions(queryMapperMock);
    }
}
