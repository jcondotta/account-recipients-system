package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginatedResult;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.mapper.LastEvaluatedKeyMapper;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.LastEvaluatedKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_query.model.CursorEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GetAccountRecipientsRepositoryImpl implements GetAccountRecipientsRepository {

    private final DynamoDbIndex<AccountRecipientEntity> dynamoDbIndex;
    private final AccountRecipientEntityMapper entityMapper;
    private final LastEvaluatedKeyMapper lastEvaluatedKeyMapper;

    private static final int DEFAULT_PAGE_LIMIT = 10;

    @Override
    public PaginatedResult<AccountRecipient> findByQuery(GetAccountRecipientsQuery query) {
        final var partitionKey = AccountRecipientEntityKey.partitionKey(query.bankAccountId());
        final var queryConditional = QueryConditional.keyEqualTo(k -> k.partitionValue(partitionKey));

        final var queryParams = query.queryParams();
        final var limit = Objects.requireNonNullElse(queryParams.limit(), DEFAULT_PAGE_LIMIT);

        LastEvaluatedKey decode = CursorEncoder.decode(queryParams.cursor());
        final var exclusiveStartKey = lastEvaluatedKeyMapper.toMap(decode);
        var queryEnhancedRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .exclusiveStartKey(exclusiveStartKey)
            .limit(limit + 1) // fetch one more item to check if there's a next page
            .build();

        var page = dynamoDbIndex.query(queryEnhancedRequest).iterator().next();
        var items = page.items().stream().map(entityMapper::toDomain).toList();

        if (items.size() > limit) {
            // Tem próxima página
            LastEvaluatedKey nextCursor = lastEvaluatedKeyMapper.toDomain(page.lastEvaluatedKey());
            return new PaginatedResult<>(items.subList(0, limit), CursorEncoder.encode(nextCursor));
        } else {
            // Última página, não retorna cursor
            return new PaginatedResult<>(items, null);
        }
    }
}