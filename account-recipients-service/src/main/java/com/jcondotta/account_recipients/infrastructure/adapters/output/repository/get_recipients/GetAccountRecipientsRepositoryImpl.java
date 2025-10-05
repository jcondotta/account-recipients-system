package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginatedResult;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.mapper.GetRecipientsLastEvaluatedKeyMapper;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.GetRecipientsLastEvaluatedKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.PaginationCursorCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GetAccountRecipientsRepositoryImpl implements GetAccountRecipientsRepository {

    private final DynamoDbIndex<AccountRecipientEntity> dynamoDbIndex;
    private final AccountRecipientEntityMapper entityMapper;
    private final GetRecipientsLastEvaluatedKeyMapper lastEvaluatedKeyMapper;

    private static final Integer DEFAULT_PAGE_LIMIT = 10;

    @Override
    public PaginatedResult<AccountRecipient> findByQuery(GetAccountRecipientsQuery query) {
        final var partitionKey = AccountRecipientEntityKey.partitionKey(query.bankAccountId());
        final var queryConditional = QueryConditional.keyEqualTo(k -> k.partitionValue(partitionKey));

        final var queryParams = query.queryParams();
        final var limit = Objects.requireNonNullElse(queryParams.limit(), DEFAULT_PAGE_LIMIT);

        var exclusiveStartKey = PaginationCursorCodec
            .decode(queryParams.cursor())
            .map(lastEvaluatedKeyMapper::toMap)
            .orElse(null);

        var page = dynamoDbIndex.query(
            QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .exclusiveStartKey(exclusiveStartKey)
                .limit(limit + 1) // sempre pede um a mais
                .build()
        ).iterator().next();

        var items = page.items().stream()
            .map(entityMapper::toDomain)
            .toList();

        String nextCursor = null;
        List<AccountRecipient> resultItems = items;

        if (items.size() > limit) {
            // devolve só até o limite
            resultItems = items.subList(0, limit);

            // cursor deve ser baseado NO ÚLTIMO ITEM RETORNADO ao cliente
            var lastReturnedItem = items.get(limit - 1);

            // aqui você monta o cursor a partir desse item (não do page.lastEvaluatedKey cru)
            GetRecipientsLastEvaluatedKey lek = new GetRecipientsLastEvaluatedKey(
                lastReturnedItem.bankAccountId().value(),
                lastReturnedItem.accountRecipientId().value(),
                lastReturnedItem.recipientName().value()
            );
            nextCursor = PaginationCursorCodec.encode(lek);
        }

        return new PaginatedResult<>(resultItems, nextCursor);
    }
}