package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginatedResult;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.PaginationCursorCodec;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.mapper.GetRecipientsLastEvaluatedKeyMapper;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.GetRecipientsLastEvaluatedKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GetAccountRecipientsRepositoryImpl implements GetAccountRecipientsRepository {

    private final DynamoDbIndex<AccountRecipientEntity> dynamoDbIndex;
    private final AccountRecipientEntityMapper entityMapper;
    private final GetRecipientsLastEvaluatedKeyMapper lastEvaluatedKeyMapper;
    private final MeterRegistry meterRegistry;

    @Override
    public PaginatedResult<AccountRecipient> findByQuery(GetAccountRecipientsQuery query) {
        final var queryParams = query.queryParams();
        final var queryConditional = buildQueryConditional(query);

        final int limit = queryParams.limit().value();
        final String cursor = Objects.nonNull(queryParams.cursor()) ? queryParams.cursor().value() : null;

        // --- decode e validação segura do cursor ---
        var exclusiveStartKey = PaginationCursorCodec
            .decode(cursor)
            .map(lastEvaluatedKeyMapper::toMap)
            .filter(map -> isValidStartKey(map, query))
            .orElse(null);

        try {
            var queryRequestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .exclusiveStartKey(exclusiveStartKey)
                .limit(limit + 1);

            var pageIterable = dynamoDbIndex.query(queryRequestBuilder.build());
            var iterator = pageIterable.iterator();

            // Se não há páginas, retorna vazio
            if (!iterator.hasNext()) {
                return new PaginatedResult<>(List.of(), null);
            }

            var firstPage = iterator.next();

            // se a página não tem itens, retorna vazio
            if (firstPage.items().isEmpty()) {
                recordEmptyResult();
                return new PaginatedResult<>(List.of(), null);
            }

            var items = firstPage.items().stream()
                .map(entityMapper::toDomain)
                .toList();

            recordItemsReturned(items.size());

            // calcula o nextCursor, se houver
            String nextCursor = null;
            List<AccountRecipient> resultItems = items;

            if (items.size() > limit) {
                resultItems = items.subList(0, limit);

                var lastReturnedItem = items.get(limit - 1);

                GetRecipientsLastEvaluatedKey lek = new GetRecipientsLastEvaluatedKey(
                    lastReturnedItem.bankAccountId().value(),
                    lastReturnedItem.accountRecipientId().value(),
                    lastReturnedItem.recipientName().value()
                );
                nextCursor = PaginationCursorCodec.encode(lek);
            }

            return new PaginatedResult<>(resultItems, nextCursor);

        } catch (DynamoDbException e) {
            log.warn("Ignoring invalid exclusiveStartKey for bankAccountId={}, cause={}",
                query.bankAccountId().value(), e.getMessage());
            return new PaginatedResult<>(List.of(), null);
        }
    }

    /**
     * Verifica se o exclusiveStartKey pertence à mesma partition key do query atual.
     */
    private boolean isValidStartKey(
        java.util.Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> map,
        GetAccountRecipientsQuery query
    ) {
        try {
            if (map == null || !map.containsKey(GetRecipientsLastEvaluatedKeyMapper.PARTITION_KEY_PARAM_NAME)) {
                return false;
            }

            var pkAttr = map.get(GetRecipientsLastEvaluatedKeyMapper.PARTITION_KEY_PARAM_NAME);
            var extractedBankAccountId = AccountRecipientEntityKey.extractBankAccountId(pkAttr.s()).value();

            // se pertence à mesma conta, é válido
            return extractedBankAccountId.equals(query.bankAccountId().value());
        } catch (Exception e) {
            log.debug("Invalid start key provided: {}", e.getMessage());
            return false;
        }
    }

    private QueryConditional buildQueryConditional(GetAccountRecipientsQuery query) {
        final var partitionKey = AccountRecipientEntityKey.partitionKey(query.bankAccountId());
        final var queryParams = query.queryParams();

        if (Objects.nonNull(queryParams.namePrefix())) {
            return QueryConditional.sortBeginsWith(k ->
                k.partitionValue(partitionKey)
                    .sortValue(queryParams.namePrefix().value())
            );
        }

        return QueryConditional.keyEqualTo(k ->
            k.partitionValue(partitionKey)
        );
    }

    private void recordItemsReturned(int count) {
        DistributionSummary.builder("account_recipients_repository_items_returned")
            .description("Number of AccountRecipients returned per query")
//            .tag("module", MODULE)
//            .tag("operation", OPERATION)
            .register(meterRegistry)
            .record(count);
    }

    private void recordEmptyResult() {
        Counter.builder("account_recipients_repository_empty_results_total")
            .description("Number of repository queries returning no items")
//            .tag("module", MODULE)
//            .tag("operation", OPERATION)
            .register(meterRegistry)
            .increment();
    }
}
