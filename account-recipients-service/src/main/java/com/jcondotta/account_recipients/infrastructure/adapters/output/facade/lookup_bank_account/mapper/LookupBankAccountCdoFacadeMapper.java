package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LookupBankAccountCdoFacadeMapper {

    Logger LOGGER = LoggerFactory.getLogger(LookupBankAccountCdoFacadeMapper.class);

    @Mapping(target = "bankAccountId", source = "bankAccountId", qualifiedByName = "mapBankAccountId")
    @Mapping(target = "accountStatus", source = "status", qualifiedByName = "mapAccountStatus")
    BankAccount map(BankAccountCdo bankAccountCdo);

    @Named("mapBankAccountId")
    default BankAccountId mapBankAccountId(UUID bankAccountId) {
        if (bankAccountId == null) {
            throw new IllegalArgumentException("Missing required field: bankAccountId");
        }
        return BankAccountId.of(bankAccountId);
    }

    @Named("mapAccountStatus")
    default AccountStatus mapAccountStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Missing required field: status");
        }
        return switch (status) {
            case "ACTIVE" -> AccountStatus.ACTIVE;
            case "PENDING" -> AccountStatus.PENDING;
            case "CANCELLED" -> AccountStatus.CANCELLED;
            default -> {
                LOGGER.warn("Received unknown status value: {}", status);
                yield AccountStatus.UNKNOWN;
            }
        };
    }
}
