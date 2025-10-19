package com.jcondotta.account_recipients.get_recipients.usecase;

import com.jcondotta.account_recipients.application.ports.output.cache.AccountRecipientsQueryCacheKey;
import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
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

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAccountRecipientsUseCaseImpl implements GetAccountRecipientsUseCase {

    private final GetAccountRecipientsQueryMapper queryMapper;
    private final CacheStore<GetAccountRecipientsResult> cacheStore;
    private final GetAccountRecipientsRepository getAccountRecipientsRepository;

    @Override
    @Observed(
        name = "account.recipients.query",
        contextualName = "queryAccountRecipients",
        lowCardinalityKeyValues = {"operation", "query"}
    )
    public GetAccountRecipientsResult execute(GetAccountRecipientsQuery query) {
        var queryCacheKey = AccountRecipientsQueryCacheKey.of(query.bankAccountId(), query.queryParams());

        return cacheStore.getIfPresent(queryCacheKey.value())
            .orElseGet(() -> {
                PaginatedResult<AccountRecipient> paginatedResult = getAccountRecipientsRepository.findByQuery(query);

                var accountRecipientDetailsList = paginatedResult.items().stream()
                    .map(queryMapper::toAccountRecipient)
                    .toList();

                var getAccountRecipientsResult = GetAccountRecipientsResult.of(accountRecipientDetailsList, paginatedResult.nextCursor());
                cacheStore.putIfAbsent(queryCacheKey.value(), getAccountRecipientsResult);

                return getAccountRecipientsResult;
            });
    }
}
