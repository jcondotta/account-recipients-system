package com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients;

import com.jcondotta.recipients.application.usecase.get_recipients.GetRecipientsUseCase;
import com.jcondotta.recipients.application.usecase.get_recipients.model.result.GetRecipientsResult;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.mapper.request.GetRecipientsRequestRestMapper;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.mapper.response.GetRecipientsResponseMapper;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.request.GetRecipientsRestRequestParams;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.response.GetRecipientsResponse;
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
  private final GetRecipientsResponseMapper responseMapper;

  @Timed(
      value = "bankAccounts.recipients.query.duration",
      description = "Time taken to retrieve recipients by bank-account-id",
      percentiles = {0.5, 0.95, 0.99})
  public ResponseEntity<GetRecipientsResponse> byQuery(
      UUID bankAccountId, GetRecipientsRestRequestParams restRequestParams) {
    GetRecipientsResult result =
        useCase.execute(requestMapper.toQuery(bankAccountId, restRequestParams));
    if (result.recipients().isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(
        responseMapper.toResponse(result.recipients(), result.nextCursor()));
  }
}
