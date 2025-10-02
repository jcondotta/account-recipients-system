package com.jcondotta.account_recipients.create_recipient;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.application.ports.output.repository.create_recipient.CreateAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.create_recipient.mapper.CreateAccountRecipientCommandMapper;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.IdempotencyKey;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.bank_account.exceptions.BankAccountNotFoundException;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.LookupBankAccountFacadeImpl;
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
class CreateAccountRecipientUseCaseImplTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);

    private static final String RECIPIENT_NAME_JEFFERSON = AccountRecipientFixtures.JEFFERSON.getRecipientName();
    private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_JEFFERSON);

    private static final String VALID_IBAN_NO_SPACES = AccountRecipientFixtures.JEFFERSON.getRecipientIban();
    private static final Iban IBAN = Iban.of(VALID_IBAN_NO_SPACES);

    private final IdempotencyKey idempotencyKey = IdempotencyKey.of(UUID.randomUUID());
    private static final Clock TEST_FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;

    private final CreateAccountRecipientCommandMapper commandMapper = CreateAccountRecipientCommandMapper.INSTANCE;

    @Mock
    private BankAccount bankAccountMock;

    @Mock
    private LookupBankAccountFacadeImpl lookupBankAccountFacadeMock;

    @Mock
    private CreateAccountRecipientRepository createAccountRecipientRepositoryMock;

    @Captor
    private ArgumentCaptor<AccountRecipient> accountRecipientCaptor;

    private CreateAccountRecipientUseCase useCase;

    @BeforeEach
    public void setUp() {
        useCase = new CreateAccountRecipientUseCaseImpl(lookupBankAccountFacadeMock, commandMapper, createAccountRecipientRepositoryMock);
    }

    @Test
    void shouldCreateRecipient_whenCommandIsValidAndBankAccountExists() {
        when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID)).thenReturn(bankAccountMock);

        var createAccountRecipientCommand = buildCreateAccountRecipientCommand();
        useCase.execute(createAccountRecipientCommand, idempotencyKey);

        verify(lookupBankAccountFacadeMock).byId(BANK_ACCOUNT_ID);
        verify(createAccountRecipientRepositoryMock).create(accountRecipientCaptor.capture());

        assertThat(accountRecipientCaptor.getValue())
            .satisfies(accountRecipient -> {
                assertThat(accountRecipient.accountRecipientId()).isNotNull();
                assertThat(accountRecipient.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(accountRecipient.recipientName()).isEqualTo(RECIPIENT_NAME);
                assertThat(accountRecipient.iban()).isEqualTo(IBAN);
                assertThat(accountRecipient.createdAt()).isEqualTo(ZonedDateTime.now(TEST_FIXED_CLOCK));
            });
    }

    @Test
    void shouldThrowBankAccountNotFoundException_whenBankAccountDoesNotExist() {
        when(lookupBankAccountFacadeMock.byId(BANK_ACCOUNT_ID))
            .thenThrow(new BankAccountNotFoundException(BANK_ACCOUNT_ID, new RuntimeException("404 simulated")));

        var createAccountRecipientCommand = buildCreateAccountRecipientCommand();

        assertThatThrownBy(() -> useCase.execute(createAccountRecipientCommand, idempotencyKey))
                .isInstanceOf(BankAccountNotFoundException.class)
                .hasMessage(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TEMPLATE);

        verify(lookupBankAccountFacadeMock).byId(BANK_ACCOUNT_ID);
        verifyNoInteractions(createAccountRecipientRepositoryMock);
    }

    private CreateAccountRecipientCommand buildCreateAccountRecipientCommand() {
        return CreateAccountRecipientCommand.of(
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            ZonedDateTime.now(TEST_FIXED_CLOCK)
        );
    }
}