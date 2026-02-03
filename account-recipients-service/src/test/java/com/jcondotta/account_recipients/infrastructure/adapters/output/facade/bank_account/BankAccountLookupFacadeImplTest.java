package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account;

import com.jcondotta.account_recipients.application.ports.output.facade.bank_account.BankAccountLookupFacade;
import com.jcondotta.account_recipients.domain.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.exceptions.BankAccountNotFoundException;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.BankAccountLookupClient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountResponseCdo;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account.mapper.BankAccountFacadeMapper;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account.mapper.BankAccountFacadeMapperImpl;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account.mapper.BankAccountFactory;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountLookupFacadeImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);

  private final BankAccountFacadeMapper mapper = new BankAccountFacadeMapperImpl(new BankAccountFactory());

  private BankAccountLookupFacade bankAccountFacade;

  @Mock
  private BankAccountLookupClient clientMock;

  @BeforeEach
  void setUp() {
    bankAccountFacade = new BankAccountLookupFacadeImpl(clientMock, mapper);
  }

  @ParameterizedTest
  @EnumSource(AccountStatus.class)
  void shouldReturnBankAccount_whenBankAccountExists(AccountStatus accountStatus) {
    var bankAccountCdo = BankAccountCdo.of(BANK_ACCOUNT_UUID, accountStatus.name());
    when(clientMock.findById(BANK_ACCOUNT_UUID))
        .thenReturn(BankAccountResponseCdo.of(bankAccountCdo));

    assertThat(bankAccountFacade.byId(BANK_ACCOUNT_ID))
        .satisfies(
            bankAccount -> {
              assertThat(bankAccount.getBankAccountId())
                  .hasToString(bankAccountCdo.bankAccountId().toString());
              assertThat(bankAccount.getAccountStatus()).hasToString(accountStatus.name());
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
    when(clientMock.findById(BANK_ACCOUNT_UUID))
        .thenThrow(FeignException.InternalServerError.class);

    assertThatThrownBy(() -> bankAccountFacade.byId(BANK_ACCOUNT_ID))
        .hasCauseInstanceOf(FeignException.InternalServerError.class)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Internal error on bank account lookup");

    verify(clientMock).findById(BANK_ACCOUNT_UUID);
  }

  @Test
  void shouldThrowRuntimeException_whenUnexpectedFeignErrorOccurs() {
    when(clientMock.findById(BANK_ACCOUNT_UUID)).thenThrow(FeignException.class);
    assertThatThrownBy(() -> bankAccountFacade.byId(BANK_ACCOUNT_ID))
        .hasCauseInstanceOf(FeignException.class)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Unexpected error on bank account lookup");


    verify(clientMock).findById(BANK_ACCOUNT_UUID);
  }
}
