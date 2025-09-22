package com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model;

import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import static java.util.Objects.requireNonNull;

public record GetAccountRecipientsQuery(BankAccountId bankAccountId) {

    public GetAccountRecipientsQuery {
        requireNonNull(bankAccountId, "bankAccountId must not be null");
    }

    public static GetAccountRecipientsQuery of(BankAccountId bankAccountId) {
        return new GetAccountRecipientsQuery(bankAccountId);
    }
}