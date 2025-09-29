package com.jcondotta.account_recipients.domain.bank_account.entity;

import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount.ACCOUNT_STATUS_NOT_NULL;
import static com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount.BANK_ACCOUNT_ID_NOT_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

    @Test
    void shouldCreateBankAccount_whenValidArguments() {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);

        assertThat(bankAccount.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(bankAccount.accountStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> new BankAccount(null, AccountStatus.ACTIVE))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(BANK_ACCOUNT_ID_NOT_NULL);
    }

    @Test
    void shouldThrowNullPointerException_whenAccountStatusIsNull() {
        assertThatThrownBy(() -> new BankAccount(BANK_ACCOUNT_ID, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(ACCOUNT_STATUS_NOT_NULL);
    }

    @Test
    void shouldCreateBankAccountUsingFactoryMethod_whenValidArguments() {
        var bankAccount = BankAccount.of(BANK_ACCOUNT_ID, AccountStatus.PENDING);

        assertThat(bankAccount.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(bankAccount.accountStatus()).isEqualTo(AccountStatus.PENDING);
    }

    @ParameterizedTest
    @EnumSource(AccountStatus.class)
    void shouldEvaluateIfBankAccountIsActive_whenStatusIsValid(AccountStatus status) {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, status);

        assertThat(bankAccount.isActive())
            .isEqualTo(status == AccountStatus.ACTIVE);
    }

    @ParameterizedTest
    @EnumSource(AccountStatus.class)
    void shouldEvaluateIfBankAccountIsPending_whenStatusIsValid(AccountStatus status) {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, status);

        assertThat(bankAccount.isPending())
            .isEqualTo(status == AccountStatus.PENDING);
    }

    @ParameterizedTest
    @EnumSource(AccountStatus.class)
    void shouldEvaluateIfBankAccountIsCancelled_whenStatusIsValid(AccountStatus status) {
        var bankAccount = new BankAccount(BANK_ACCOUNT_ID, status);

        assertThat(bankAccount.isCancelled())
            .isEqualTo(status == AccountStatus.CANCELLED);
    }
}
