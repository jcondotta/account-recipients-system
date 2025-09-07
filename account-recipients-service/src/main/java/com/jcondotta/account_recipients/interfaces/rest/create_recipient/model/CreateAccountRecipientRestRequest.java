package com.jcondotta.account_recipients.interfaces.rest.create_recipient.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRecipientRestRequest(

    @NotNull
    UUID bankAccountId,

    @NotBlank
    String recipientName,

    @NotBlank
    String recipientIban
) {}