package com.jcondotta.account_recipients.get_recipients.controller.model.response;

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