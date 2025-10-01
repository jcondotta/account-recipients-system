package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model;

import java.util.UUID;

public record LastEvaluatedKey(UUID bankAccountId, UUID accountRecipientId, String recipientName) { }