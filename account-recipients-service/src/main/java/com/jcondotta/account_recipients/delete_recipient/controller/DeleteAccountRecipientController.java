package com.jcondotta.account_recipients.delete_recipient.controller;

import com.jcondotta.account_recipients.infrastructure.interfaces.rest.headers.HttpHeadersCustom;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("${api.v1.account-recipients.recipient-id-path}")
public interface DeleteAccountRecipientController {

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> deleteAccountRecipient(
      @RequestHeader(name = HttpHeadersCustom.IDEMPOTENCY_KEY) UUID idempotencyKey,
      @PathVariable("bank-account-id") UUID bankAccountId,
      @PathVariable("recipient-id") UUID recipientId);
}
