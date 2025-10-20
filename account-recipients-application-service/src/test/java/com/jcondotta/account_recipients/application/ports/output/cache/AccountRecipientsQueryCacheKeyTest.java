package com.jcondotta.account_recipients.application.ports.output.cache;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.RecipientNamePrefix;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.QueryLimit;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;

import java.security.MessageDigest;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class AccountRecipientsQueryCacheKeyTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

    private static final QueryLimit QUERY_LIMIT_20 = QueryLimit.of(20);
    private static final RecipientNamePrefix RECIPIENT_NAME_PREFIX = RecipientNamePrefix.of("jeff");
    private static final PaginationCursor PAGINATION_CURSOR = PaginationCursor.of("encoded-cursor-123");

    @ParameterizedTest(name = "{index} => limit={0}, namePrefix={1}, cursor={2}")
    @ArgumentsSource(CacheKeyArgumentsProvider.class)
    void shouldCreateCacheKey_whenBankAccountIdAndQueryParamsAreValid(
        QueryLimit limit, RecipientNamePrefix namePrefix, PaginationCursor cursor) {

        var queryParams = new GetAccountRecipientsQueryParams(limit, namePrefix, cursor);

        var queryCacheKey = AccountRecipientsQueryCacheKey.of(BANK_ACCOUNT_ID, queryParams);

        var expectedPrefix = String.format(AccountRecipientsRootCacheKey.PREFIX_TEMPLATE, BANK_ACCOUNT_ID);
        var expectedValue = String.format(AccountRecipientsQueryCacheKey.ACCOUNT_RECIPIENTS_TEMPLATE, BANK_ACCOUNT_ID,
            AccountRecipientsQueryCacheKey.queryParamsHash(queryParams));

        assertThat(queryCacheKey)
            .satisfies(cacheKey -> {
                assertThat(cacheKey.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(cacheKey.queryParams()).isEqualTo(queryParams);
                assertThat(cacheKey.value()).isEqualTo(expectedValue);
                assertThat(cacheKey.rootPrefix()).isEqualTo(expectedPrefix);
                assertThat(cacheKey).isInstanceOf(AccountRecipientsCacheKey.class);
            });

        var queryCacheKeyViaOf = AccountRecipientsQueryCacheKey.of(BANK_ACCOUNT_ID, queryParams);
        assertThat(queryCacheKeyViaOf).isEqualTo(queryCacheKey);
    }

    static class CacheKeyArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(QUERY_LIMIT_20, RECIPIENT_NAME_PREFIX, PAGINATION_CURSOR),
                Arguments.of(QUERY_LIMIT_20, RECIPIENT_NAME_PREFIX, null),
                Arguments.of(QUERY_LIMIT_20, null, PAGINATION_CURSOR),
                Arguments.of(QUERY_LIMIT_20, null, null)
            );
        }
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        var queryParams = new GetAccountRecipientsQueryParams(QUERY_LIMIT_20, RECIPIENT_NAME_PREFIX, PAGINATION_CURSOR);
        assertThatThrownBy(() -> AccountRecipientsQueryCacheKey.of(null, queryParams))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(AccountRecipientsQueryCacheKey.BANK_ACCOUNT_ID_NOT_NULL_MESSAGE);
    }

    @Test
    void shouldThrowNullPointerException_whenQueryParamsIsNull() {
        assertThatThrownBy(() -> AccountRecipientsQueryCacheKey.of(BANK_ACCOUNT_ID, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(AccountRecipientsQueryCacheKey.QUERY_PARAMS_NOT_NULL_MESSAGE);
    }

    @Test
    void shouldThrowIllegalStateException_whenHashingFails() {
        var queryParams = new GetAccountRecipientsQueryParams(QUERY_LIMIT_20, RECIPIENT_NAME_PREFIX, PAGINATION_CURSOR);

        try (var mocked = Mockito.mockStatic(MessageDigest.class)) {
            mocked.when(() -> MessageDigest.getInstance("SHA-256"))
                .thenThrow(new RuntimeException("something went wrong"));

            assertThatThrownBy(() -> AccountRecipientsQueryCacheKey.queryParamsHash(queryParams))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(AccountRecipientsQueryCacheKey.QUERY_HASH_ERROR_MESSAGE)
                .hasCauseInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("something went wrong");
        }
    }
}
