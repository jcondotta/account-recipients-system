package com.jcondotta.recipients.infrastructure.adapters.output.facade.bank_account.mapper;

import com.jcondotta.recipients.domain.entities.BankAccount;
import com.jcondotta.recipients.domain.enums.AccountStatus;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.infrastructure.adapters.output.client.lookup_bank_account.model.BankAccountCdo;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ObjectFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class BankAccountFactory {

    @ObjectFactory
    public BankAccount create(BankAccountCdo source) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(source.bankAccountId(), "bank account id value must not be null");
        Objects.requireNonNull(source.status(), "status value must not be null");

        return BankAccount.restore(
            BankAccountId.of(source.bankAccountId()),
            mapToAccountStatus(source.status())
        );
    }

    private AccountStatus mapToAccountStatus(String status) {
        return switch (status) {
            case "ACTIVE" -> AccountStatus.ACTIVE;
            case "PENDING" -> AccountStatus.PENDING;
            case "CANCELLED" -> AccountStatus.CANCELLED;
            default -> {
                log.warn("Received unknown status value: {}", status);
                yield AccountStatus.UNKNOWN;
            }
        };
    }
}
