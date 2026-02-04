package com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient;

import com.jcondotta.recipients.application.usecase.create_recipient.CreateRecipientUseCase;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.mapper.CreateRecipientRequestRestMapper;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.recipients.infrastructure.properties.RecipientURIProperties;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class CreateRecipientControllerImpl implements CreateRecipientController {

  private final CreateRecipientUseCase useCase;
  private final CreateRecipientRequestRestMapper mapper;
  private final RecipientURIProperties uriProperties;

  @Override
  @Timed(
      value = "bankAccounts.recipients.create.time",
      description = "recipient creation time measurement",
      percentiles = {0.5, 0.95, 0.99})
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> createAccountRecipient(UUID idempotencyKey, UUID bankAccountId, CreateRecipientRestRequest request) {
    var command = mapper.toCommand(bankAccountId, request);
    useCase.execute(command);

    return ResponseEntity.created(uriProperties.recipientsURI(bankAccountId)).build();
  }
}
