package com.jcondotta.account_recipients.interfaces.rest.create_recipient;

import com.jcondotta.account_recipients.interfaces.rest.create_recipient.model.CreateAccountRecipientRestRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.UUID;

@RequestMapping("${api.v1.account-recipients.root-path}")
public interface CreateAccountRecipientController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> createAccountRecipient(
        @PathVariable("bank-account-id") UUID bankAccountId,
        @Valid @RequestBody CreateAccountRecipientRestRequest request,
        @RequestHeader(name = "X-Idempotency-Key") UUID idempotencyKey
    );
}