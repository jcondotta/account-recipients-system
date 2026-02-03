package com.jcondotta.account_recipients.create_recipient.usecase;

import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientCreatedEventPublisher;
import com.jcondotta.account_recipients.application.ports.output.repository.create_recipient.CreateRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.factory.ClockTestFactory;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.entities.BankAccount;
import com.jcondotta.account_recipients.domain.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.exceptions.BankAccountNotFoundException;
import com.jcondotta.account_recipients.domain.entities.Recipient;
import com.jcondotta.account_recipients.domain.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.value_objects.Iban;
import com.jcondotta.account_recipients.domain.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account.BankAccountLookupFacadeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRecipientUseCaseImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);

  private static final String RECIPIENT_NAME_JEFFERSON = AccountRecipientFixtures.JEFFERSON.getRecipientName();
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_JEFFERSON);

  private static final String VALID_IBAN_NO_SPACES = AccountRecipientFixtures.JEFFERSON.getRecipientIban();

  private static final Iban IBAN = Iban.of(VALID_IBAN_NO_SPACES);
  private static final Clock CLOCK_FIXED = ClockTestFactory.TEST_CLOCK_FIXED;
  private final IdempotencyKey idempotencyKey = IdempotencyKey.newKey();

  @Mock
  private BankAccountLookupFacadeImpl lookupBankAccountFacadeMock;

  @Mock
  private CreateRecipientRepository createRecipientRepositoryMock;

  @Mock
  private RecipientCreatedEventPublisher recipientCreatedEventPublisherMock;

  @Captor
  private ArgumentCaptor<Recipient> accountRecipientCaptor;

  @Captor
  private ArgumentCaptor<RecipientCreatedEvent> recipientCreatedEventCaptor;

  private CreateRecipientUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase =
        new CreateRecipientUseCaseImpl(
            lookupBankAccountFacadeMock,
            createRecipientRepositoryMock,
            recipientCreatedEventPublisherMock,
            CLOCK_FIXED
        );
  }

  @Test
  void shouldCreateRecipient_whenCommandIsValidAndBankAccountExists() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);

    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);

    var createAccountRecipientCommand = buildCreateAccountRecipientCommand();
    useCase.execute(createAccountRecipientCommand, idempotencyKey);

    verify(createRecipientRepositoryMock).create(accountRecipientCaptor.capture());

    assertThat(accountRecipientCaptor.getValue())
        .satisfies(
            recipient -> {
              assertThat(recipient.getRecipientId()).isNotNull();
              assertThat(recipient.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(recipient.getRecipientName()).isEqualTo(RECIPIENT_NAME);
              assertThat(recipient.getIban()).isEqualTo(IBAN);
              assertThat(recipient.getCreatedAt())
                  .isEqualTo(ZonedDateTime.now(CLOCK_FIXED));
            });

    verify(recipientCreatedEventPublisherMock).send(recipientCreatedEventCaptor.capture(), eq(idempotencyKey));
    assertThat(recipientCreatedEventCaptor.getValue())
        .satisfies(
            recipientCreatedEvent -> {
              assertThat(recipientCreatedEvent.recipientId()).isNotNull();
              assertThat(recipientCreatedEvent.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(recipientCreatedEvent.recipientName()).isEqualTo(RECIPIENT_NAME);
              assertThat(recipientCreatedEvent.iban()).isEqualTo(IBAN);
              assertThat(recipientCreatedEvent.occurredAt()).isEqualTo(ZonedDateTime.now(CLOCK_FIXED));
            });

    verify(lookupBankAccountFacadeMock).byId(BANK_ACCOUNT_ID);

    verifyNoMoreInteractions(
        lookupBankAccountFacadeMock,
        createRecipientRepositoryMock,
        recipientCreatedEventPublisherMock
    );
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenBankAccountDoesNotExist() {
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID))
        .thenThrow(
            new BankAccountNotFoundException(
                BANK_ACCOUNT_ID, new RuntimeException("404 simulated")));

    var createAccountRecipientCommand = buildCreateAccountRecipientCommand();

    assertThatThrownBy(() -> useCase.execute(createAccountRecipientCommand, idempotencyKey))
        .isInstanceOf(BankAccountNotFoundException.class)
        .hasMessage(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TEMPLATE);

    verify(lookupBankAccountFacadeMock).byId(BANK_ACCOUNT_ID);
    verifyNoInteractions(
        createRecipientRepositoryMock, recipientCreatedEventPublisherMock);
  }

  @Test
  void shouldThrowNullPointerException_whenCommandIsNull() {
    assertThatThrownBy(() -> useCase.execute(null, idempotencyKey))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("command must not be null");

    verifyNoInteractions(
        lookupBankAccountFacadeMock,
        createRecipientRepositoryMock,
        recipientCreatedEventPublisherMock
    );
  }

  @Test
  void shouldThrowNullPointerException_whenIdempotencyKeyIsNull() {
    var command = buildCreateAccountRecipientCommand();

    assertThatThrownBy(() -> useCase.execute(command, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("idempotencyKey must not be null");

    verifyNoInteractions(
        lookupBankAccountFacadeMock,
        createRecipientRepositoryMock,
        recipientCreatedEventPublisherMock
    );
  }

  @Test
  void shouldPropagateException_whenEventPublishingFails() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);
    doThrow(new RuntimeException("Kinesis down"))
        .when(recipientCreatedEventPublisherMock)
        .send(any(), any());

    var command = buildCreateAccountRecipientCommand();

    assertThatThrownBy(() -> useCase.execute(command, idempotencyKey))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Kinesis down");
  }

  private CreateRecipientCommand buildCreateAccountRecipientCommand() {
    return CreateRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);
  }
}
