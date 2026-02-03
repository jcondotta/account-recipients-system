package com.jcondotta.account_recipients.get_recipients.usecase;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.GetRecipientsRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.model.PaginatedResult;
import com.jcondotta.account_recipients.application.usecase.get_recipients.GetRecipientsUseCase;
import com.jcondotta.account_recipients.application.usecase.get_recipients.mapper.GetRecipientsQueryMapper;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetRecipientsResult;
import com.jcondotta.account_recipients.domain.entities.Recipient;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetRecipientsUseCaseImpl implements GetRecipientsUseCase {

  private final GetRecipientsQueryMapper queryMapper;
  private final GetRecipientsRepository getRecipientsRepository;

  @Override
  @Observed(
      name = "account.recipients.query",
      contextualName = "queryAccountRecipients",
      lowCardinalityKeyValues = {"operation", "query"})
  public GetRecipientsResult execute(GetRecipientsQuery query) {
    PaginatedResult<Recipient> paginatedResult =
        getRecipientsRepository.findByQuery(query);

    var accountRecipientDetailsList = paginatedResult.items()
        .stream()
        .map(queryMapper::toRecipient)
        .toList();

    return GetRecipientsResult.of(accountRecipientDetailsList, paginatedResult.nextCursor());
  }
}
