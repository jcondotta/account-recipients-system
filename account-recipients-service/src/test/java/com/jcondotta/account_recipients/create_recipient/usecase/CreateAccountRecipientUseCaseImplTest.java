package com.jcondotta.account_recipients.create_recipient.usecase;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.application.events.mapper.RecipientCreatedEventMapper;
import com.jcondotta.account_recipients.application.ports.output.cache.AccountRecipientsRootCacheKey;
import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientCreatedEventPublisher;
import com.jcondotta.account_recipients.application.ports.output.repository.create_recipient.CreateAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.bank_account.exceptions.BankAccountNotFoundException;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.LookupBankAccountFacadeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
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
class CreateAccountRecipientUseCaseImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);

  private static final String RECIPIENT_NAME_JEFFERSON =
      AccountRecipientFixtures.JEFFERSON.getRecipientName();
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_JEFFERSON);

  private static final String VALID_IBAN_NO_SPACES =
      AccountRecipientFixtures.JEFFERSON.getRecipientIban();
  private static final Iban IBAN = Iban.of(VALID_IBAN_NO_SPACES);
  private static final Clock TEST_FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;
  private final IdempotencyKey idempotencyKey = IdempotencyKey.newKey();

  private final RecipientCreatedEventMapper recipientCreatedEventMapper = Mappers.getMapper(RecipientCreatedEventMapper.class);

  @Mock
  private BankAccount bankAccountMock;

  @Mock
  private LookupBankAccountFacadeImpl lookupBankAccountFacadeMock;

  @Mock
  private CreateAccountRecipientRepository createAccountRecipientRepositoryMock;

  @Mock
  private CacheStore<GetAccountRecipientsResult> cacheStoreMock;

  @Mock
  private RecipientCreatedEventPublisher recipientCreatedEventPublisherMock;

  @Captor
  private ArgumentCaptor<AccountRecipient> accountRecipientCaptor;

  @Captor
  private ArgumentCaptor<RecipientCreatedEvent> recipientCreatedEventCaptor;

  private CreateAccountRecipientUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase =
        new CreateAccountRecipientUseCaseImpl(
            lookupBankAccountFacadeMock,
            createAccountRecipientRepositoryMock,
            recipientCreatedEventPublisherMock,
            recipientCreatedEventMapper,
            cacheStoreMock,
            TEST_FIXED_CLOCK
        );
  }

  @Test
  void shouldCreateRecipient_whenCommandIsValidAndBankAccountExists() {
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccountMock);
    when(createAccountRecipientRepositoryMock.create(any(AccountRecipient.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var createAccountRecipientCommand = buildCreateAccountRecipientCommand();
    useCase.execute(createAccountRecipientCommand, idempotencyKey);

    verify(createAccountRecipientRepositoryMock).create(accountRecipientCaptor.capture());

    assertThat(accountRecipientCaptor.getValue())
        .satisfies(
            accountRecipient -> {
              assertThat(accountRecipient.getRecipientId()).isNotNull();
              assertThat(accountRecipient.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(accountRecipient.getRecipientName()).isEqualTo(RECIPIENT_NAME);
              assertThat(accountRecipient.getIban()).isEqualTo(IBAN);
              assertThat(accountRecipient.getCreatedAt())
                  .isEqualTo(ZonedDateTime.now(TEST_FIXED_CLOCK));
            });

    verify(recipientCreatedEventPublisherMock).send(recipientCreatedEventCaptor.capture(), eq(idempotencyKey));
    assertThat(recipientCreatedEventCaptor.getValue())
        .satisfies(
            recipientCreatedEvent -> {
              assertThat(recipientCreatedEvent.recipientId()).isNotNull();
              assertThat(recipientCreatedEvent.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(recipientCreatedEvent.recipientName()).isEqualTo(RECIPIENT_NAME);
              assertThat(recipientCreatedEvent.iban()).isEqualTo(IBAN);
              assertThat(recipientCreatedEvent.occurredAt()).isEqualTo(ZonedDateTime.now(TEST_FIXED_CLOCK));
            });

    var cacheKey = AccountRecipientsRootCacheKey.of(BANK_ACCOUNT_ID);
    verify(cacheStoreMock).evictKeysByPrefix(cacheKey.value());
    verify(lookupBankAccountFacadeMock).byId(BANK_ACCOUNT_ID);

    verifyNoMoreInteractions(
        lookupBankAccountFacadeMock,
        cacheStoreMock,
        createAccountRecipientRepositoryMock,
        recipientCreatedEventPublisherMock);
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
        createAccountRecipientRepositoryMock, cacheStoreMock, recipientCreatedEventPublisherMock);
  }

  @Test
  void shouldThrowNullPointerException_whenCommandIsNull() {
    assertThatThrownBy(() -> useCase.execute(null, idempotencyKey))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("command must not be null");

    verifyNoInteractions(
        lookupBankAccountFacadeMock,
        createAccountRecipientRepositoryMock,
        cacheStoreMock,
        recipientCreatedEventPublisherMock);
  }

  @Test
  void shouldThrowNullPointerException_whenIdempotencyKeyIsNull() {
    var command = buildCreateAccountRecipientCommand();

    assertThatThrownBy(() -> useCase.execute(command, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("idempotencyKey must not be null");

    verifyNoInteractions(
        lookupBankAccountFacadeMock,
        createAccountRecipientRepositoryMock,
        cacheStoreMock,
        recipientCreatedEventPublisherMock);
  }

  @Test
  void shouldPropagateException_whenEventPublishingFails() {
    when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccountMock);
    doThrow(new RuntimeException("Kafka down"))
        .when(recipientCreatedEventPublisherMock)
        .send(any(), any());

    var command = buildCreateAccountRecipientCommand();

    assertThatThrownBy(() -> useCase.execute(command, idempotencyKey))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Kafka down");
  }

  private CreateAccountRecipientCommand buildCreateAccountRecipientCommand() {
    return CreateAccountRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);
  }
}
