package com.jcondotta.recipients.infrastructure.adapters.output.facade.bank_account.mapper;

import com.jcondotta.recipients.domain.entities.BankAccount;
import com.jcondotta.recipients.infrastructure.adapters.output.client.bank_account.model.BankAccountCdo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = { BankAccountFactory.class },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BankAccountFacadeMapper {

  BankAccount map(BankAccountCdo bankAccountCdo);
}
