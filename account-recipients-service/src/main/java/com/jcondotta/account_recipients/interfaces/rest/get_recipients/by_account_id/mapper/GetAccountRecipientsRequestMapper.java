package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.mapper;

import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
    }
)
public interface GetAccountRecipientsRequestMapper {

    @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(bankAccountId))")
    GetAccountRecipientsQuery toQuery(UUID bankAccountId);
}
