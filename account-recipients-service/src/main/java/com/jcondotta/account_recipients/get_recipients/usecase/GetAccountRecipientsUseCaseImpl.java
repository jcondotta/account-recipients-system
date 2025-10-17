package com.jcondotta.account_recipients.get_recipients.usecase;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginatedResult;
import com.jcondotta.account_recipients.application.usecase.get_recipients.GetAccountRecipientsUseCase;
import com.jcondotta.account_recipients.application.usecase.get_recipients.mapper.GetAccountRecipientsQueryMapper;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAccountRecipientsUseCaseImpl implements GetAccountRecipientsUseCase {

    private final GetAccountRecipientsQueryMapper queryMapper;
    private final GetAccountRecipientsRepository getAccountRecipientsRepository;

    @Override
    @Observed(
        name = "account_recipients.query",
        contextualName = "queryAccountRecipients",
        lowCardinalityKeyValues = {"module", "account-recipients", "operation", "query"}
    )
    public GetAccountRecipientsResult execute(GetAccountRecipientsQuery getAccountRecipientsQuery) {
        PaginatedResult<AccountRecipient> paginatedResult = getAccountRecipientsRepository.findByQuery(getAccountRecipientsQuery);

        var accountRecipientDetailsList = paginatedResult.items().stream()
            .map(queryMapper::toAccountRecipient)
            .toList();

        return GetAccountRecipientsResult.of(accountRecipientDetailsList, paginatedResult.nextCursor());
    }
}
