package com.jcondotta.account_recipients.get_recipients.controller.model.response;

import java.util.List;

public record GetAccountRecipientsResponse(List<AccountRecipientResponse> accountRecipients, String nextCursor) {

    public static GetAccountRecipientsResponse of(List<AccountRecipientResponse> accountRecipients, String nextCursor) {
        return new GetAccountRecipientsResponse(accountRecipients, nextCursor);
    }
}