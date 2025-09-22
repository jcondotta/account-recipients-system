package com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record GetAccountRecipientsResult(List<AccountRecipientDetails> accountRecipients){

    public GetAccountRecipientsResult {
        requireNonNull(accountRecipients, "accountRecipients must not be null");
    }

    public static GetAccountRecipientsResult of(List<AccountRecipientDetails> accountRecipients) {
        return new GetAccountRecipientsResult(accountRecipients);
    }
}