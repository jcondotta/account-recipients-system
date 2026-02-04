package com.jcondotta.recipients.application.usecase.create_recipient;

import com.jcondotta.recipients.application.common.fixtures.RecipientFixtures;
import com.jcondotta.recipients.application.helper.ClockTestFactory;
import com.jcondotta.recipients.application.ports.output.facade.bank_account.BankAccountLookupFacade;
import com.jcondotta.recipients.application.ports.output.repository.create_recipient.CreateRecipientRepository;
import com.jcondotta.recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.recipients.domain.entities.BankAccount;
import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.enums.AccountStatus;
import com.jcondotta.recipients.domain.exceptions.BankAccountNotFoundException;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
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

  private static final String RECIPIENT_NAME_JEFFERSON = RecipientFixtures.JEFFERSON.getRecipientName();
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_JEFFERSON);

  private static final String VALID_IBAN_NO_SPACES = RecipientFixtures.JEFFERSON.getIban();

  private static final Iban IBAN = Iban.of(VALID_IBAN_NO_SPACES);
  private static final Clock CLOCK_FIXED = ClockTestFactory.TEST_CLOCK_FIXED;

  @Mock
  private BankAccountLookupFacade bankAccountLookupFacade;

  @Mock
  private CreateRecipientRepository createRecipientRepository;

  @Captor
  private ArgumentCaptor<Recipient> recipientCaptor;

  private CreateRecipientUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase =
        new CreateRecipientUseCaseImpl(
            bankAccountLookupFacade,
            createRecipientRepository,
            CLOCK_FIXED
        );
  }

  @Test
  void shouldCreateRecipient_whenCommandIsValidAndBankAccountExists() {
    BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);

    when(bankAccountLookupFacade.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccount);

    var createAccountRecipientCommand = buildCreateAccountRecipientCommand();
    useCase.execute(createAccountRecipientCommand);

    verify(createRecipientRepository).create(recipientCaptor.capture());

    assertThat(recipientCaptor.getValue())
        .satisfies(
            recipient -> {
              assertThat(recipient.getRecipientId()).isNotNull();
              assertThat(recipient.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
              assertThat(recipient.getRecipientName()).isEqualTo(RECIPIENT_NAME);
              assertThat(recipient.getIban()).isEqualTo(IBAN);
              assertThat(recipient.getCreatedAt())
                  .isEqualTo(ZonedDateTime.now(CLOCK_FIXED));
            });

    verify(bankAccountLookupFacade).byId(BANK_ACCOUNT_ID);
    verifyNoMoreInteractions(bankAccountLookupFacade, createRecipientRepository);
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenBankAccountDoesNotExist() {
    when(bankAccountLookupFacade.byId(BANK_ACCOUNT_ID))
        .thenThrow(
            new BankAccountNotFoundException(
                BANK_ACCOUNT_ID, new RuntimeException("404 simulated")));

    var createAccountRecipientCommand = buildCreateAccountRecipientCommand();

    assertThatThrownBy(() -> useCase.execute(createAccountRecipientCommand))
        .isInstanceOf(BankAccountNotFoundException.class)
        .hasMessage(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TEMPLATE);

    verify(bankAccountLookupFacade).byId(BANK_ACCOUNT_ID);
    verifyNoInteractions(createRecipientRepository);
  }

  @Test
  void shouldThrowNullPointerException_whenCommandIsNull() {
    assertThatThrownBy(() -> useCase.execute(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("command must not be null");

    verifyNoInteractions(bankAccountLookupFacade, createRecipientRepository);
  }

  private CreateRecipientCommand buildCreateAccountRecipientCommand() {
    return CreateRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);
  }
}
