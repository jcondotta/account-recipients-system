package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LookupBankAccountCdoFacadeMapper {

    LookupBankAccountCdoFacadeMapper INSTANCE = Mappers.getMapper(LookupBankAccountCdoFacadeMapper.class);

    @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(bankAccountCdo.bankAccountId()))")
    @Mapping(target = "accountStatus", expression = "java(AccountStatus.valueOf(bankAccountCdo.status().name()))")
    BankAccount map(BankAccountCdo bankAccountCdo);

}
