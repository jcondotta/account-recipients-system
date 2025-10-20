package com.jcondotta.account_recipients.get_recipients.controller.model.request;

public record GetAccountRecipientsRestRequestParams(Integer limit, String namePrefix, String cursor) {

    public static GetAccountRecipientsRestRequestParams of(Integer limit, String namePrefix, String cursor) {
        return new GetAccountRecipientsRestRequestParams(limit, namePrefix, cursor);
    }
}
