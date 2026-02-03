package com.jcondotta.recipients.delete_recipient.controller;

import com.jcondotta.recipients.application.usecase.delete_recipient.DeleteRecipientUseCase;
import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.recipients.delete_recipient.controller.mapper.DeleteRecipientRequestMapper;
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
public class DeleteRecipientControllerImpl implements DeleteRecipientController {

  private final DeleteRecipientRequestMapper requestMapper;
  private final DeleteRecipientUseCase useCase;

  @Override
  @Timed(
      value = "account.recipients.delete.time",
      description = "account recipient creation time measurement",
      percentiles = {0.5, 0.95, 0.99})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteAccountRecipient(UUID idempotencyKey, UUID bankAccountId, UUID recipientId) {
    useCase.execute(requestMapper.toCommand(bankAccountId, recipientId), IdempotencyKey.of(idempotencyKey));
    return ResponseEntity.noContent().build();
  }
}
