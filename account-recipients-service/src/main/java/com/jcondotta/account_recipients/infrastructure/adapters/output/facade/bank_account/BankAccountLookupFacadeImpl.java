package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account;

import com.jcondotta.account_recipients.application.ports.output.facade.bank_account.BankAccountLookupFacade;
import com.jcondotta.account_recipients.domain.entities.BankAccount;
import com.jcondotta.account_recipients.domain.exceptions.BankAccountNotFoundException;
import com.jcondotta.account_recipients.domain.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.BankAccountLookupClient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account.mapper.BankAccountFacadeMapper;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BankAccountLookupFacadeImpl implements BankAccountLookupFacade {

  private final BankAccountLookupClient client;
  private final BankAccountFacadeMapper mapper;

  @Override
  public BankAccount byId(BankAccountId bankAccountId) {
    try {
      return mapper.map(client.findById(bankAccountId.value()).bankAccountCdo());
    }
    catch (FeignException.NotFound e) {
      log.warn("Bank account not found: {}", bankAccountId.value());
      throw new BankAccountNotFoundException(bankAccountId, e);
    }
    catch (FeignException.InternalServerError e) {
      log.error(
          "Internal server error while fetching bank account: {}. Reason: {}",
          bankAccountId.value(),
          e.getMessage(),
          e);
      throw new IllegalStateException("Internal error on bank account lookup", e);
    }
    catch (FeignException e) {
      log.error(
          "Unexpected Feign error while fetching bank account: {}. Status: {}, Message: {}",
          bankAccountId.value(),
          e.status(),
          e.getMessage(),
          e);
      throw new IllegalStateException("Unexpected error on bank account lookup", e);
    }
  }
}
