package com.jcondotta.recipients.infrastructure.adapters.input.rest.delete_recipient;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@RequestMapping("${api.v1.recipients.recipient-id-path}")
public interface DeleteRecipientController {

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> deleteAccountRecipient(
      @PathVariable("bank-account-id") UUID bankAccountId,
      @PathVariable("recipient-id") UUID recipientId
  );
}
