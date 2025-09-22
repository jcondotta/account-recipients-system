package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account;

import com.jcondotta.account_recipients.application.ports.output.facade.lookup_bank_account.LookupBankAccountFacade;
import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.bank_account.exceptions.BankAccountNotFoundException;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.LookupBankAccountClient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper.LookupBankAccountCdoFacadeMapper;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class LookupBankAccountFacadeImpl implements LookupBankAccountFacade {

    private final LookupBankAccountClient client;
    private final LookupBankAccountCdoFacadeMapper mapper;

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
            log.error("Internal server error while fetching bank account: {}. Reason: {}", bankAccountId.value(), e.getMessage(), e);
            throw new RuntimeException("Internal error on bank account lookup", e);
        }
        catch (FeignException e) {
            log.error("Unexpected Feign error while fetching bank account: {}. Status: {}, Message: {}", bankAccountId.value(), e.status(), e.getMessage(), e);
            throw new RuntimeException("Unexpected error on bank account lookup", e);
        }
    }
}


