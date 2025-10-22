package com.jcondotta.account_recipients.create_recipient.controller;

import com.jcondotta.account_recipients.create_recipient.controller.model.CreateAccountRecipientRestRequest;
import com.jcondotta.account_recipients.infrastructure.interfaces.rest.headers.HttpHeadersCustom;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("${api.v1.account-recipients.root-path}")
public interface CreateAccountRecipientController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> createAccountRecipient(
        @RequestHeader(name = HttpHeadersCustom.IDEMPOTENCY_KEY) UUID idempotencyKey,
        @PathVariable("bank-account-id") UUID bankAccountId,
        @Valid @RequestBody CreateAccountRecipientRestRequest request
    );
}