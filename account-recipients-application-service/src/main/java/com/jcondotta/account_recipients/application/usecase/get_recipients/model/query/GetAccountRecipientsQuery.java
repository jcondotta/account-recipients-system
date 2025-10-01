package com.jcondotta.account_recipients.application.usecase.get_recipients.model.query;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import static java.util.Objects.requireNonNull;

public record GetAccountRecipientsQuery(BankAccountId bankAccountId, GetAccountRecipientsQueryParams queryParams) {

    public GetAccountRecipientsQuery {
        requireNonNull(bankAccountId, "bankAccountId must not be null");
        requireNonNull(queryParams, "queryParams must not be null");
    }

    public static GetAccountRecipientsQuery of(BankAccountId bankAccountId, GetAccountRecipientsQueryParams queryParams) {
        return new GetAccountRecipientsQuery(bankAccountId, queryParams);
    }
}