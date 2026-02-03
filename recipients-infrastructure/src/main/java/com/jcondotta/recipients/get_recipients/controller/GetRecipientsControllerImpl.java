package com.jcondotta.recipients.get_recipients.controller;

import com.jcondotta.recipients.application.usecase.get_recipients.GetRecipientsUseCase;
import com.jcondotta.recipients.application.usecase.get_recipients.model.result.GetRecipientsResult;
import com.jcondotta.recipients.get_recipients.controller.mapper.request.GetRecipientsRequestRestMapper;
import com.jcondotta.recipients.get_recipients.controller.mapper.response.GetAccountRecipientsResponseMapper;
import com.jcondotta.recipients.get_recipients.controller.model.request.GetRecipientsRestRequestParams;
import com.jcondotta.recipients.get_recipients.controller.model.response.GetRecipientsResponse;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class GetRecipientsControllerImpl implements GetRecipientsController {

  private final GetRecipientsUseCase useCase;
  private final GetRecipientsRequestRestMapper requestMapper;
  private final GetAccountRecipientsResponseMapper responseMapper;

  @Timed(
      value = "account.recipients.query.duration",
      description = "Time taken to retrieve account recipients by bank-account-id",
      percentiles = {0.5, 0.95, 0.99})
  public ResponseEntity<GetRecipientsResponse> byQuery(
      UUID bankAccountId, GetRecipientsRestRequestParams restRequestParams) {
    GetRecipientsResult result =
        useCase.execute(requestMapper.toQuery(bankAccountId, restRequestParams));
    if (result.accountRecipients().isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(
        responseMapper.toResponse(result.accountRecipients(), result.nextCursor()));
  }
}
