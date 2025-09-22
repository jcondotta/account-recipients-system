package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.model;

import java.util.List;

public record GetAccountRecipientsResponse(List<AccountRecipientResponse> accountRecipients) {

    public static GetAccountRecipientsResponse of(List<AccountRecipientResponse> accountRecipients) {
        return new GetAccountRecipientsResponse(accountRecipients);
    }
}