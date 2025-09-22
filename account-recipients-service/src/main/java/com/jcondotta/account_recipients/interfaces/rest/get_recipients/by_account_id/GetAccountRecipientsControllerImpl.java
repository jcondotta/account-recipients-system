package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id;

import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.GetAccountRecipientsUseCase;
import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.mapper.GetAccountRecipientsRequestMapper;
import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.mapper.GetAccountRecipientsResponseMapper;
import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.model.GetAccountRecipientsResponse;
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

    @Override
    public ResponseEntity<GetAccountRecipientsResponse> byAccountId(UUID bankAccountId) {
        GetAccountRecipientsResult result = useCase.execute(requestMapper.toQuery(bankAccountId));
        return ResponseEntity.ok(responseMapper.toResponse(result.accountRecipients()));
    }
}