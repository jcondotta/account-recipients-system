package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account;

import com.jcondotta.account_recipients.application.ports.output.facade.lookup_bank_account.LookupBankAccountFacade;
import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.bank_account.exceptions.BankAccountNotFoundException;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.LookupBankAccountClient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountResponseCdo;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper.LookupBankAccountCdoFacadeMapper;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LookupBankAccountFacadeImplTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);

    private final LookupBankAccountCdoFacadeMapper mapper = Mappers.getMapper(LookupBankAccountCdoFacadeMapper.class);
    private LookupBankAccountFacade bankAccountFacade;

    @Mock
    private LookupBankAccountClient clientMock;

    @BeforeEach
    void setUp() {
        bankAccountFacade = new LookupBankAccountFacadeImpl(clientMock, mapper);
    }

    @ParameterizedTest
    @EnumSource(AccountStatus.class)
    void shouldReturnBankAccount_whenBankAccountExists(AccountStatus accountStatus) {
        var bankAccountCdo = BankAccountCdo.of(BANK_ACCOUNT_UUID, accountStatus.name());
        when(clientMock.findById(BANK_ACCOUNT_UUID))
            .thenReturn(BankAccountResponseCdo.of(bankAccountCdo));

        assertThat(bankAccountFacade.byId(BANK_ACCOUNT_ID))
            .satisfies(bankAccount -> {
                assertThat(bankAccount.bankAccountId()).hasToString(bankAccountCdo.bankAccountId().toString());
                assertThat(bankAccount.accountStatus()).hasToString(accountStatus.name());
            });

        verify(clientMock).findById(BANK_ACCOUNT_UUID);
    }

    @Test
    void shouldThrowBankAccountNotFoundException_whenFeignNotFoundOccurs() {
        when(clientMock.findById(BANK_ACCOUNT_UUID)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> bankAccountFacade.byId(BANK_ACCOUNT_ID))
            .hasCauseInstanceOf(FeignException.NotFound.class)
            .isInstanceOf(BankAccountNotFoundException.class);

        verify(clientMock).findById(BANK_ACCOUNT_UUID);
    }

    @Test
    void shouldThrowRuntimeException_whenFeignInternalServerErrorOccurs() {
        when(clientMock.findById(BANK_ACCOUNT_UUID)).thenThrow(FeignException.InternalServerError.class);

        assertThatThrownBy(() -> bankAccountFacade.byId(BANK_ACCOUNT_ID))
            .hasCauseInstanceOf(FeignException.InternalServerError.class)
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Internal error on bank account lookup");

        verify(clientMock).findById(BANK_ACCOUNT_UUID);
    }

    @Test
    void shouldThrowRuntimeException_whenUnexpectedFeignErrorOccurs() {
        when(clientMock.findById(BANK_ACCOUNT_UUID)).thenThrow(FeignException.class);

        assertThatThrownBy(() -> bankAccountFacade.byId(BANK_ACCOUNT_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unexpected error on bank account lookup")
            .hasCauseInstanceOf(FeignException.class);

        verify(clientMock).findById(BANK_ACCOUNT_UUID);
    }
}