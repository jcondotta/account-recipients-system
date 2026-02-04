package com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient;

import com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("${api.v1.recipients.root-path}")
public interface CreateRecipientController {

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<String> createAccountRecipient(
      @PathVariable("bank-account-id") UUID bankAccountId,
      @Valid @RequestBody CreateRecipientRestRequest request
  );
}
