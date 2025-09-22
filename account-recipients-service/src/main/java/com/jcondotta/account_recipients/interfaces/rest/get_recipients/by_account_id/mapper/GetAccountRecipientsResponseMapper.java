package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.mapper;

import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model.AccountRecipientDetails;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.interfaces.rest.create_recipient.model.CreateAccountRecipientRestRequest;
import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.model.AccountRecipientResponse;
import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.model.GetAccountRecipientsResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Clock;
import java.util.List;

@Mapper(
    componentModel = "spring",
    imports = {
        BankAccountId.class,
        RecipientName.class,
        Iban.class,
    }
)
public interface GetAccountRecipientsResponseMapper {

    @Mapping(target = "accountRecipientId", source = "accountRecipientDetails.accountRecipientId.value")
    @Mapping(target = "bankAccountId", source = "accountRecipientDetails.bankAccountId.value")
    @Mapping(target = "recipientName", source = "accountRecipientDetails.recipientName.value")
    @Mapping(target = "iban", source = "accountRecipientDetails.iban.value")
    AccountRecipientResponse toAccountRecipientResponse(AccountRecipientDetails accountRecipientDetails);

    default GetAccountRecipientsResponse toResponse(List<AccountRecipientDetails> accountRecipientDetailsList){
        return new GetAccountRecipientsResponse(
            accountRecipientDetailsList.stream()
                .map(this::toAccountRecipientResponse)
                .toList()
        );
    }
}
