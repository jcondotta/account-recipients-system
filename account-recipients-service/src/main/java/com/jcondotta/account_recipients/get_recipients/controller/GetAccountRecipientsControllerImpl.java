package com.jcondotta.account_recipients.get_recipients.controller;

import com.jcondotta.account_recipients.application.usecase.get_recipients.GetAccountRecipientsUseCase;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.get_recipients.controller.mapper.request.GetAccountRecipientsRequestMapper;
import com.jcondotta.account_recipients.get_recipients.controller.mapper.response.GetAccountRecipientsResponseMapper;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.GetAccountRecipientsResponse;
import com.jcondotta.account_recipients.get_recipients.controller.model.request.GetAccountRecipientsRestRequestParams;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class GetAccountRecipientsControllerImpl implements GetAccountRecipientsController {

    private final GetAccountRecipientsUseCase useCase;
    private final GetAccountRecipientsRequestMapper requestMapper;
    private final GetAccountRecipientsResponseMapper responseMapper;

    @Timed(
        value = "account_recipient.query.duration",
        description = "Time taken to retrieve account recipients by bank-account-id",
        percentiles = {0.5, 0.95, 0.99}
    )
    public ResponseEntity<GetAccountRecipientsResponse> byQuery(UUID bankAccountId, GetAccountRecipientsRestRequestParams restRequestParams) {
        GetAccountRecipientsResult result = useCase.execute(requestMapper.toQuery(bankAccountId, restRequestParams));
        if(result.accountRecipients().isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(responseMapper.toResponse(result.accountRecipients(), result.nextCursor()));
    }
}