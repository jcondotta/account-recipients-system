package com.jcondotta.recipients.infrastructure.adapters.input.rest.delete_recipient;

import com.jcondotta.recipients.infrastructure.adapters.input.rest.common.headers.HttpHeadersCustom;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("${api.v1.recipients.recipient-id-path}")
public interface DeleteRecipientController {

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> deleteAccountRecipient(
      @RequestHeader(name = HttpHeadersCustom.IDEMPOTENCY_KEY) UUID idempotencyKey,
      @PathVariable("bank-account-id") UUID bankAccountId,
      @PathVariable("recipient-id") UUID recipientId);
}
