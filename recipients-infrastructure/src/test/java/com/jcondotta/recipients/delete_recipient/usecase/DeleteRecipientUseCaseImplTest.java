package com.jcondotta.recipients.delete_recipient.usecase;

import com.jcondotta.recipients.application.ports.output.messaging.RecipientDeletedEventPublisher;
import com.jcondotta.recipients.application.ports.output.repository.delete_recipient.DeleteRecipientRepository;
import com.jcondotta.recipients.application.ports.output.repository.get_recipient.GetRecipientRepository;
import com.jcondotta.recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;
import com.jcondotta.recipients.common.factory.ClockTestFactory;
import com.jcondotta.recipients.common.fixtures.RecipientFixtures;
import com.jcondotta.recipients.domain.entities.BankAccount;
import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.enums.AccountStatus;
import com.jcondotta.recipients.domain.events.RecipientDeletedEvent;
import com.jcondotta.recipients.domain.exceptions.BankAccountNotActiveException;
import com.jcondotta.recipients.domain.exceptions.RecipientNotFoundException;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import com.jcondotta.recipients.infrastructure.adapters.output.facade.bank_account.BankAccountLookupFacadeImpl;
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
class DeleteRecipientUseCaseImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
  private static final RecipientId RECIPIENT_ID = RecipientId.of(ACCOUNT_RECIPIENT_UUID);

  private static final String RECIPIENT_NAME_JEFFERSON = RecipientFixtures.JEFFERSON.getRecipientName();
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_JEFFERSON);

  private static final String VALID_IBAN_NO_SPACES = RecipientFixtures.JEFFERSON.getRecipientIban();

  private static final Iban IBAN = Iban.of(VALID_IBAN_NO_SPACES);

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;

  @Mock
  private BankAccountLookupFacadeImpl lookupBankAccountFacadeMock;

  @Mock
  private GetRecipientRepository getRecipientRepository;

  @Mock
  private DeleteRecipientRepository deleteRecipientRepository;

  @Mock
  private RecipientDeletedEventPublisher deletedEventPublisher;

  @Captor
  private ArgumentCaptor<Recipient> recipientCaptor;

  @Captor
  private ArgumentCaptor<RecipientDeletedEvent> recipientDeletedEventCaptor;

  private DeleteRecipientUseCaseImpl useCase;

  @BeforeEach
  void setUp() {
    useCase = new DeleteRecipientUseCaseImpl(
        lookupBankAccountFacadeMock,
        getRecipientRepository,
        deleteRecipientRepository,
        deletedEventPublisher,
        FIXED_CLOCK
    );
  }

  @Test
  void shouldDeleteRecipientAndPublishEvent_whenCommandIsValid() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    Recipient recipient = Recipient.restore(
        RECIPIENT_ID,
        BANK_ACCOUNT_ID,
        RECIPIENT_NAME,
        IBAN,
        ZonedDateTime.now(FIXED_CLOCK)
    );

    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);
    when(getRecipientRepository.getRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(recipient));

    var command = DeleteRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);
    useCase.execute(command);

    verify(deleteRecipientRepository).delete(recipient);

    verify(deletedEventPublisher).publish(recipientDeletedEventCaptor.capture());
    assertThat(recipientDeletedEventCaptor.getValue())
        .satisfies(
            recipientDeletedEvent -> {
              assertThat(recipientDeletedEvent.recipientId()).isEqualTo(RECIPIENT_ID);
              assertThat(recipientDeletedEvent.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(recipientDeletedEvent.occurredAt()).isEqualTo(ZonedDateTime.now(FIXED_CLOCK));
            });

    verifyNoMoreInteractions(deleteRecipientRepository, deletedEventPublisher);
  }

  @Test
  void shouldThrowAccountRecipientNotFoundException_whenRecipientDoesNotExist() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);

    var command = DeleteRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(getRecipientRepository.getRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(RecipientNotFoundException.class);

    verify(deleteRecipientRepository, never()).delete(any());
    verifyNoInteractions(deletedEventPublisher);
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
  void shouldThrowBankAccountNotActiveException_whenBankAccountIsNotActive(AccountStatus accountStatus) {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, accountStatus);
    Recipient recipient = Recipient.restore(
        RECIPIENT_ID,
        BANK_ACCOUNT_ID,
        RECIPIENT_NAME,
        IBAN,
        ZonedDateTime.now(FIXED_CLOCK)
    );

    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);
    when(getRecipientRepository.getRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(recipient));

    var command = DeleteRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(BankAccountNotActiveException.class)
        .hasMessage("recipient.cannotBeDeleted.bankAccountNotActive");

    verify(deleteRecipientRepository, never()).delete(any());
    verifyNoInteractions(deletedEventPublisher);
  }

  @Test
  void shouldThrowIllegalStateException_whenRecipientDoesNotBelongToBankAccount() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    Recipient recipient = Recipient.restore(
        RECIPIENT_ID,
        BankAccountId.of(UUID.randomUUID()),
        RECIPIENT_NAME,
        IBAN,
        ZonedDateTime.now(FIXED_CLOCK)
    );

    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);
    when(getRecipientRepository.getRecipient(BANK_ACCOUNT_ID, RECIPIENT_ID))
        .thenReturn(Optional.of(recipient));

    var command = DeleteRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Recipient does not belong to this account");

    verify(deleteRecipientRepository, never()).delete(any());
    verifyNoInteractions(deletedEventPublisher);
  }

  @Test
  void shouldThrowException_whenCommandIsNull() {
    assertThatThrownBy(() -> useCase.execute(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Command must not be null");

    verifyNoInteractions(
        getRecipientRepository,
        deleteRecipientRepository,
        deletedEventPublisher
    );
  }
}
