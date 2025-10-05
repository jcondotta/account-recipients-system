package com.jcondotta.account_recipients.delete_recipient.controller;

import com.jcondotta.account_recipients.application.usecase.delete_recipient.DeleteAccountRecipientUseCase;
import com.jcondotta.account_recipients.delete_recipient.controller.mapper.DeleteAccountRecipientRequestMapper;
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
public class DeleteAccountRecipientControllerImpl implements DeleteAccountRecipientController {

    private final DeleteAccountRecipientRequestMapper requestMapper;
    private final DeleteAccountRecipientUseCase useCase;

    @Override
    @Timed(
        value = "account-recipient.delete.time",
        description = "account recipient creation time measurement",
        percentiles = {0.5, 0.95, 0.99}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAccountRecipient(UUID bankAccountId, UUID accountRecipientId) {
        useCase.execute(requestMapper.toCommand(bankAccountId, accountRecipientId));

        return ResponseEntity.noContent().build();
    }
}