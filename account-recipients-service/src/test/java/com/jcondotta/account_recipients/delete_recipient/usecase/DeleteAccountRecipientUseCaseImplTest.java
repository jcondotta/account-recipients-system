package com.jcondotta.account_recipients.delete_recipient.usecase;

import com.jcondotta.account_recipients.application.events.mapper.RecipientDeletedEventMapper;
import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientDeletedEventPublisher;
import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipient.GetAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.factory.ClockTestFactory;
import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.recipient.exceptions.AccountRecipientNotFoundException;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.LookupBankAccountFacadeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountRecipientUseCaseImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
  private static final RecipientId RECIPIENT_ID = RecipientId.of(ACCOUNT_RECIPIENT_UUID);

  private static final IdempotencyKey IDEMPOTENCY_KEY = IdempotencyKey.newKey();

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;

  @Mock
  private LookupBankAccountFacadeImpl lookupBankAccountFacadeMock;

  @Mock
  private AccountRecipient accountRecipientMock;

  @Mock
  private GetAccountRecipientRepository getAccountRecipientRepository;

  @Mock
  private DeleteAccountRecipientRepository deleteAccountRecipientRepository;

  @Mock
  private RecipientDeletedEventPublisher deletedEventPublisher;

  @Mock
  private RecipientDeletedEventMapper eventMapper;

  @Captor
  private ArgumentCaptor<AccountRecipient> accountRecipientCaptor;

  private DeleteAccountRecipientUseCaseImpl useCase;

  @BeforeEach
  void setUp() {
    useCase = new DeleteAccountRecipientUseCaseImpl(
        lookupBankAccountFacadeMock,
        getAccountRecipientRepository,
        deleteAccountRecipientRepository,
        deletedEventPublisher,
        eventMapper,
        FIXED_CLOCK
    );
  }

  @Test
  void shouldDeleteRecipientAndPublishEvent_whenCommandIsValid() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);

    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(accountRecipientMock.getBankAccountId()).thenReturn(BANK_ACCOUNT_ID);
    when(getAccountRecipientRepository.getAccountRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(accountRecipientMock));

    var event = new RecipientDeletedEvent(RECIPIENT_ID, BANK_ACCOUNT_ID, ZonedDateTime.now(FIXED_CLOCK));
    when(eventMapper.fromAccountRecipient(accountRecipientMock)).thenReturn(event);

    useCase.execute(command, IDEMPOTENCY_KEY);

    verify(accountRecipientMock).delete(FIXED_CLOCK);
    verify(deletedEventPublisher).send(event, IDEMPOTENCY_KEY);
    verify(deleteAccountRecipientRepository).delete(accountRecipientCaptor.capture());

    assertThat(accountRecipientCaptor.getValue()).isEqualTo(accountRecipientMock);


    verifyNoMoreInteractions(
        deleteAccountRecipientRepository,
        deletedEventPublisher
    );
  }

  @Test
  void shouldThrowAccountRecipientNotFoundException_whenRecipientDoesNotExist() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);

    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(getAccountRecipientRepository.getAccountRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(command, IDEMPOTENCY_KEY))
        .isInstanceOf(AccountRecipientNotFoundException.class);

    verify(deleteAccountRecipientRepository, never()).delete(any());
    verifyNoInteractions(deletedEventPublisher);
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
  void shouldThrowIllegalStateException_whenBankAccountIsNotActive(AccountStatus accountStatus) {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, accountStatus);
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);

    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(accountRecipientMock.getBankAccountId()).thenReturn(BANK_ACCOUNT_ID);
    when(getAccountRecipientRepository.getAccountRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(accountRecipientMock));

    assertThatThrownBy(() -> useCase.execute(command, IDEMPOTENCY_KEY))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Cannot delete recipient for non-active account");

    verify(deleteAccountRecipientRepository, never()).delete(any());
    verifyNoInteractions(deletedEventPublisher);
  }

  @Test
  void shouldThrowIllegalStateException_whenRecipientDoesNotBelongToBankAccount() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);

    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(accountRecipientMock.getBankAccountId())
        .thenReturn(BankAccountId.of(UUID.randomUUID()));

    when(getAccountRecipientRepository.getAccountRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(accountRecipientMock));

    assertThatThrownBy(() -> useCase.execute(command, IDEMPOTENCY_KEY))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Recipient does not belong to this account");

    verify(deleteAccountRecipientRepository, never()).delete(any());
    verifyNoInteractions(deletedEventPublisher);
  }

  @Test
  void shouldThrowException_whenCommandIsNull() {
    assertThatThrownBy(() -> useCase.execute(null, IDEMPOTENCY_KEY))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Command must not be null");

    verifyNoInteractions(
        getAccountRecipientRepository,
        deleteAccountRecipientRepository,
        deletedEventPublisher
    );
  }
}
