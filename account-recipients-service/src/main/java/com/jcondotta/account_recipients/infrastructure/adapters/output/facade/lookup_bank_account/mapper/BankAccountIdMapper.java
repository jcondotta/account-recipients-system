package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BankAccountIdMapper {

    BankAccountIdMapper INSTANCE = Mappers.getMapper(BankAccountIdMapper.class);

    @Named("mapToBankAccountId")
    default BankAccountId mapToBankAccountId(UUID bankAccountId) {
        Objects.requireNonNull(bankAccountId, "bank account id value must not be null");
        return BankAccountId.of(bankAccountId);
    }
}
