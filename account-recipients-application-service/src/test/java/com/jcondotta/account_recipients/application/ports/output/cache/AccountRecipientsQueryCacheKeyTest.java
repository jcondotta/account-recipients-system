package com.jcondotta.account_recipients.application.ports.output.cache;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.QueryLimit;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AccountRecipientsQueryCacheKeyTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final QueryLimit QUERY_LIMIT_20 = QueryLimit.of(20);
    private static final PaginationCursor PAGINATION_CURSOR = PaginationCursor.of("encoded-cursor-123");

    @Test
    void shouldCreateCacheKey_whenBankAccountIdAndQueryParamsAreValid() {
        var queryParams = new GetAccountRecipientsQueryParams(QUERY_LIMIT_20, PAGINATION_CURSOR);

        var expectedPrefix = String.format(AccountRecipientsQueryCacheKey.PREFIX_TEMPLATE, BANK_ACCOUNT_ID.value());
        var expectedValue = String.format(AccountRecipientsQueryCacheKey.ACCOUNT_RECIPIENTS_TEMPLATE, BANK_ACCOUNT_ID.value(), queryParams);

        var queryCacheKey = new AccountRecipientsQueryCacheKey(BANK_ACCOUNT_ID, queryParams);

        assertThat(queryCacheKey)
            .satisfies(cacheKey -> {
                assertThat(cacheKey.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(cacheKey.queryParams()).isEqualTo(queryParams);
//                assertThat(cacheKey.value()).isEqualTo(expectedValue);
                assertThat(cacheKey.rootPrefix()).isEqualTo(expectedPrefix);
                assertThat(cacheKey).isInstanceOf(AccountRecipientsCacheKey.class);
            });

        var queryCacheKeyViaOf = AccountRecipientsQueryCacheKey.of(BANK_ACCOUNT_ID, queryParams);
        assertThat(queryCacheKeyViaOf).isEqualTo(queryCacheKey);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        var queryParams = new GetAccountRecipientsQueryParams(QUERY_LIMIT_20, PAGINATION_CURSOR);
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
}
