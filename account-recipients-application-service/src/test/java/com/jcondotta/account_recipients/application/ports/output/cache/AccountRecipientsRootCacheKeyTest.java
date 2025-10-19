package com.jcondotta.account_recipients.application.ports.output.cache;

import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountRecipientsRootCacheKeyTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

    @Test
    void shouldCreateCacheKey_whenBankAccountIdIsNotNull() {
        var rootCacheKey = new AccountRecipientsRootCacheKey(BANK_ACCOUNT_ID);
        var expectedValue = String.format(AccountRecipientsRootCacheKey.PREFIX_TEMPLATE, BANK_ACCOUNT_ID);

        assertThat(rootCacheKey)
            .satisfies(cacheKey -> {
                assertThat(cacheKey.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(cacheKey.value()).isEqualTo(expectedValue);
                assertThat(cacheKey.rootPrefix()).isEqualTo(expectedValue);
                assertThat(cacheKey.toString()).isEqualTo(expectedValue);
                assertThat(cacheKey).isInstanceOf(AccountRecipientsCacheKey.class);
            });

        var rootCacheKeyViaOf = AccountRecipientsRootCacheKey.of(BANK_ACCOUNT_ID);
        assertThat(rootCacheKeyViaOf).isEqualTo(rootCacheKey);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> new AccountRecipientsRootCacheKey(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(AccountRecipientsRootCacheKey.BANK_ACCOUNT_ID_NOT_NULL_MESSAGE);
    }
}