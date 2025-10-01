package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_query.model;

import java.util.UUID;

public record AccountRecipientResponse(
    UUID accountRecipientId,
    UUID bankAccountId,
    String recipientName,
    String iban) {

    public static AccountRecipientResponse of(UUID accountRecipientId, UUID bankAccountId, String recipientName, String iban) {
        return new AccountRecipientResponse(accountRecipientId, bankAccountId, recipientName, iban);
    }
}