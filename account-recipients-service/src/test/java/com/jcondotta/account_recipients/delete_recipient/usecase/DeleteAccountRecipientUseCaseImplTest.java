package com.jcondotta.account_recipients.delete_recipient.usecase;

import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientDeletedEventPublisher;
import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipient.GetAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.factory.ClockTestFactory;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.recipient.exceptions.AccountRecipientNotFoundException;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
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

  private static final String RECIPIENT_NAME_JEFFERSON = AccountRecipientFixtures.JEFFERSON.getRecipientName();
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_JEFFERSON);

  private static final String VALID_IBAN_NO_SPACES = AccountRecipientFixtures.JEFFERSON.getRecipientIban();

  private static final Iban IBAN = Iban.of(VALID_IBAN_NO_SPACES);

  private static final IdempotencyKey IDEMPOTENCY_KEY = IdempotencyKey.newKey();

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;

  @Mock
  private LookupBankAccountFacadeImpl lookupBankAccountFacadeMock;

  @Mock
  private GetAccountRecipientRepository getAccountRecipientRepository;

  @Mock
  private DeleteAccountRecipientRepository deleteAccountRecipientRepository;

  @Mock
  private RecipientDeletedEventPublisher deletedEventPublisher;

  @Captor
  private ArgumentCaptor<AccountRecipient> accountRecipientCaptor;

  @Captor
  private ArgumentCaptor<RecipientDeletedEvent> recipientDeletedEventCaptor;

  private DeleteAccountRecipientUseCaseImpl useCase;

  @BeforeEach
  void setUp() {
    useCase = new DeleteAccountRecipientUseCaseImpl(
        lookupBankAccountFacadeMock,
        getAccountRecipientRepository,
        deleteAccountRecipientRepository,
        deletedEventPublisher,
        FIXED_CLOCK
    );
  }

  @Test
  void shouldDeleteRecipientAndPublishEvent_whenCommandIsValid() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    AccountRecipient accountRecipient = AccountRecipient.restore(
        RECIPIENT_ID,
        BANK_ACCOUNT_ID,
        RECIPIENT_NAME,
        IBAN,
        ZonedDateTime.now(FIXED_CLOCK)
    );

    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);
    when(getAccountRecipientRepository.getAccountRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(accountRecipient));

    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);
    useCase.execute(command, IDEMPOTENCY_KEY);

    verify(deleteAccountRecipientRepository).delete(accountRecipient);

    verify(deletedEventPublisher).send(recipientDeletedEventCaptor.capture(), eq(IDEMPOTENCY_KEY));
    assertThat(recipientDeletedEventCaptor.getValue())
        .satisfies(
            recipientDeletedEvent -> {
              assertThat(recipientDeletedEvent.recipientId()).isEqualTo(RECIPIENT_ID);
              assertThat(recipientDeletedEvent.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(recipientDeletedEvent.occurredAt()).isEqualTo(ZonedDateTime.now(FIXED_CLOCK));
            });

    verifyNoMoreInteractions(deleteAccountRecipientRepository, deletedEventPublisher);
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
    AccountRecipient accountRecipient = AccountRecipient.restore(
        RECIPIENT_ID,
        BANK_ACCOUNT_ID,
        RECIPIENT_NAME,
        IBAN,
        ZonedDateTime.now(FIXED_CLOCK)
    );

    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);
    when(getAccountRecipientRepository.getAccountRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(accountRecipient));

    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    assertThatThrownBy(() -> useCase.execute(command, IDEMPOTENCY_KEY))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Cannot delete recipient for non-active account");

    verify(deleteAccountRecipientRepository, never()).delete(any());
    verifyNoInteractions(deletedEventPublisher);
  }

  @Test
  void shouldThrowIllegalStateException_whenRecipientDoesNotBelongToBankAccount() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    AccountRecipient accountRecipient = AccountRecipient.restore(
        RECIPIENT_ID,
        BankAccountId.of(UUID.randomUUID()),
        RECIPIENT_NAME,
        IBAN,
        ZonedDateTime.now(FIXED_CLOCK)
    );

    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);
    when(getAccountRecipientRepository.getAccountRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(accountRecipient));

    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

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
