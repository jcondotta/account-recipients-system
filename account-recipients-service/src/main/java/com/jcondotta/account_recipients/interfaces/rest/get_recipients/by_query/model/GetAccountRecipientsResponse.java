package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_query.model;

import java.util.List;

public record GetAccountRecipientsResponse(List<AccountRecipientResponse> accountRecipients, String nextCursor) {

    public static GetAccountRecipientsResponse of(List<AccountRecipientResponse> accountRecipients, String nextCursor) {
        return new GetAccountRecipientsResponse(accountRecipients, nextCursor);
    }
}