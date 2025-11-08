package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
    uses = {
        BankAccountIdMapper.class,
        AccountStatusMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BankAccountFacadeMapper {

    @Mapping(target = "bankAccountId", source = "bankAccountId", qualifiedByName = "mapToBankAccountId")
    @Mapping(target = "accountStatus", source = "status", qualifiedByName = "mapToAccountStatus")
    BankAccount map(BankAccountCdo bankAccountCdo);
}
