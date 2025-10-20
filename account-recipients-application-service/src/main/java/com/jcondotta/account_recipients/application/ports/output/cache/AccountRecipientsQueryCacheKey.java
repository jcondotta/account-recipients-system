package com.jcondotta.account_recipients.application.ports.output.cache;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.RecipientNamePrefix;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginationCursor;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public record AccountRecipientsQueryCacheKey(BankAccountId bankAccountId, GetAccountRecipientsQueryParams queryParams)
        implements AccountRecipientsCacheKey {

    public static final String ACCOUNT_RECIPIENTS_TEMPLATE = PREFIX_TEMPLATE + ":%s";
    public static final String QUERY_PARAMS_HASH_TEMPLATE = "limit=%s&namePrefix=%s&cursor=%s";

    public static final String BANK_ACCOUNT_ID_NOT_NULL_MESSAGE = "bank account id must not be null";
    public static final String QUERY_PARAMS_NOT_NULL_MESSAGE = "query params must not be null";

    public static final String QUERY_HASH_ERROR_MESSAGE = "Failed to generate cache key hash";

    public static AccountRecipientsQueryCacheKey of(BankAccountId bankAccountId, GetAccountRecipientsQueryParams queryParams) {
        requireNonNull(bankAccountId, BANK_ACCOUNT_ID_NOT_NULL_MESSAGE);
        requireNonNull(queryParams, QUERY_PARAMS_NOT_NULL_MESSAGE);

        return new AccountRecipientsQueryCacheKey(bankAccountId, queryParams);
    }

    @Override
    public String value() {
        return String.format(ACCOUNT_RECIPIENTS_TEMPLATE, bankAccountId.value(), queryParamsHash(queryParams));
    }

    public static String queryParamsHash(GetAccountRecipientsQueryParams queryParams) {
        try {
            final String raw = buildRawQueryParamsKey(queryParams);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(raw.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(encoded)
                .substring(0, 10);
        }
        catch (Exception e) {
            throw new IllegalStateException(QUERY_HASH_ERROR_MESSAGE, e);
        }
    }

    private static String buildRawQueryParamsKey(GetAccountRecipientsQueryParams queryParams) {
        final var limitValue = queryParams.limit().value();
        final var cursorValue = extractCursorValue(queryParams.cursor());
        final var namePrefixValue = extractNamePrefixValue(queryParams.namePrefix());

        return String.format(QUERY_PARAMS_HASH_TEMPLATE, limitValue, namePrefixValue, cursorValue);
    }

    private static String extractCursorValue(PaginationCursor cursor){
        return Optional.ofNullable(cursor)
            .map(Object::toString)
            .orElse("null");
    }

    private static String extractNamePrefixValue(RecipientNamePrefix namePrefix){
        return Optional.ofNullable(namePrefix)
            .map(Object::toString)
            .orElse("null");
    }
}
