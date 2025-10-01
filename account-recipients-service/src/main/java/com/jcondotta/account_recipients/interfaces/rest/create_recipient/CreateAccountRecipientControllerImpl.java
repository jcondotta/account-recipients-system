package com.jcondotta.account_recipients.interfaces.rest.create_recipient;

import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.shared.IdempotencyKey;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientURIProperties;
import com.jcondotta.account_recipients.interfaces.rest.create_recipient.mapper.CreateAccountRecipientRequestRestMapper;
import com.jcondotta.account_recipients.interfaces.rest.create_recipient.model.CreateAccountRecipientRestRequest;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Clock;
import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class CreateAccountRecipientControllerImpl implements CreateAccountRecipientController {

    private final CreateAccountRecipientUseCase useCase;
    private final CreateAccountRecipientRequestRestMapper mapper;
    private final AccountRecipientURIProperties uriProperties;
    private final Clock clock;

    @Override
    @Timed(
        value = "account-recipient.create.time",
        description = "account recipient creation time measurement",
        percentiles = {0.5, 0.95, 0.99}
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createAccountRecipient(
        UUID bankAccountId, CreateAccountRecipientRestRequest request, UUID idempotencyKey
    ) {
        var requestCardCommand = mapper.toCommand(bankAccountId, request, clock);
        useCase.execute(requestCardCommand, IdempotencyKey.of(idempotencyKey));

        return ResponseEntity.created(
            uriProperties.accountRecipientsURI(bankAccountId)
        ).build();
    }
}