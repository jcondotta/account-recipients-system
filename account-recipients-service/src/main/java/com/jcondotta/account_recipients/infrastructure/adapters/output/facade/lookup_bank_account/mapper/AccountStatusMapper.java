package com.jcondotta.account_recipients.infrastructure.adapters.output.facade.lookup_bank_account.mapper;

import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface AccountStatusMapper {

    Logger LOGGER = LoggerFactory.getLogger(AccountStatusMapper.class);

    AccountStatusMapper INSTANCE = Mappers.getMapper(AccountStatusMapper.class);

    @Named("mapToAccountStatus")
    default AccountStatus mapToAccountStatus(String status) {
        Objects.requireNonNull(status, "status value must not be null");

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
