package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.bank_account.mapper;

import com.jcondotta.account_recipients.domain.entities.BankAccount;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = { BankAccountFactory.class },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BankAccountFacadeMapper {

  BankAccount map(BankAccountCdo bankAccountCdo);
}
