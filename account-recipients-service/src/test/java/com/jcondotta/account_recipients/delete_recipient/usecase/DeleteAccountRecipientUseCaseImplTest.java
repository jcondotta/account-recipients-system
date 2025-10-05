package com.jcondotta.account_recipients.delete_recipient.usecase;

import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountRecipientUseCaseImplTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.of(ACCOUNT_RECIPIENT_UUID);

    @Mock
    private DeleteAccountRecipientRepository repositoryMock;

    @Captor
    private ArgumentCaptor<BankAccountId> bankAccountIdCaptor;

    @Captor
    private ArgumentCaptor<AccountRecipientId> accountRecipientIdCaptor;

    private DeleteAccountRecipientUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteAccountRecipientUseCaseImpl(repositoryMock);
    }

    @Test
    void shouldDeleteRecipient_whenCommandIsValid() {
        var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, ACCOUNT_RECIPIENT_ID);

        useCase.execute(command);

        verify(repositoryMock).delete(bankAccountIdCaptor.capture(), accountRecipientIdCaptor.capture());
        verifyNoMoreInteractions(repositoryMock);

        assertThat(bankAccountIdCaptor.getValue()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(accountRecipientIdCaptor.getValue()).isEqualTo(ACCOUNT_RECIPIENT_ID);
    }

    @Test
    void shouldNotInteractWithRepository_whenCommandIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Command must not be null");

        verifyNoInteractions(repositoryMock);
    }
}
