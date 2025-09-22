package com.jcondotta.account_recipients.interfaces.rest.create_recipient.model;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountRecipientRestRequest(

    @NotBlank
    String recipientName,

    @NotBlank
    String iban
) {}