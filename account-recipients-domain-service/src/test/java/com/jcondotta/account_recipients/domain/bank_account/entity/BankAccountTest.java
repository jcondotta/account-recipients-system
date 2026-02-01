package com.jcondotta.account_recipients.domain.bank_account.entity;

import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount.ACCOUNT_STATUS_NOT_NULL;
import static com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount.BANK_ACCOUNT_ID_NOT_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountTest {

    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

    private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of("Jefferson Condotta");
    private static final Iban IBAN = Iban.of("GB82WEST12345698765432");

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);

    @Test
    void shouldCreateBankAccount_whenValidArguments() {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);

        assertThat(bankAccount.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        assertThatThrownBy(() -> BankAccount.restore(null, AccountStatus.ACTIVE))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(BANK_ACCOUNT_ID_NOT_NULL);
    }

    @Test
    void shouldThrowNullPointerException_whenAccountStatusIsNull() {
        assertThatThrownBy(() -> BankAccount.restore(BANK_ACCOUNT_ID, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(ACCOUNT_STATUS_NOT_NULL);
    }

    @ParameterizedTest
    @EnumSource(AccountStatus.class)
    void shouldEvaluateIfBankAccountIsActive_whenStatusIsValid(AccountStatus status) {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, status);

        assertThat(bankAccount.isActive()).isEqualTo(status == AccountStatus.ACTIVE);
    }

    @ParameterizedTest
    @EnumSource(AccountStatus.class)
    void shouldEvaluateIfBankAccountIsPending_whenStatusIsValid(AccountStatus status) {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, status);

        assertThat(bankAccount.isPending()).isEqualTo(status == AccountStatus.PENDING);
    }

    @ParameterizedTest
    @EnumSource(AccountStatus.class)
    void shouldEvaluateIfBankAccountIsCancelled_whenStatusIsValid(AccountStatus status) {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, status);

        assertThat(bankAccount.isCancelled()).isEqualTo(status == AccountStatus.CANCELLED);
    }

    @Test
    void shouldCreateRecipient_whenAccountIsActive() {
        BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);

        AccountRecipient recipient = bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);

        assertThat(recipient.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(recipient.getRecipientId()).isNotNull();
        assertThat(recipient.getRecipientName()).isEqualTo(RECIPIENT_NAME_JEFFERSON);
        assertThat(recipient.getIban()).isEqualTo(IBAN);
        assertThat(recipient.getCreatedAt()).isEqualTo(ZonedDateTime.now(CLOCK));
        assertThat(recipient.getDeletedAt()).isNull();
    }

    @ParameterizedTest
    @EnumSource(value = AccountStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"ACTIVE"})
    void shouldThrowIllegalStateException_whenAccountIsNotActive(AccountStatus accountStatus) {
        BankAccount bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, accountStatus);

        assertThatThrownBy(() -> bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot create recipient for non-active account");
    }

    @Test
    void shouldBeEqual_whenBankAccountIdIsSame() {
        BankAccount account1 = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
        BankAccount account2 = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.CANCELLED);

        assertThat(account1)
            .isEqualTo(account2)
            .hasSameHashCodeAs(account2);
    }

    @Test
    void shouldNotBeEqual_whenBankAccountIdIsDifferent() {
        BankAccount account1 = BankAccount.restore(BankAccountId.of(UUID.randomUUID()), AccountStatus.ACTIVE);
        BankAccount account2 = BankAccount.restore(BankAccountId.of(UUID.randomUUID()), AccountStatus.ACTIVE);

        assertThat(account1).isNotEqualTo(account2);
    }

    @Test
    void shouldBeEqualToItself() {
        BankAccount account = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
        assertThat(account).isEqualTo(account);
    }

    @Test
    void shouldNotBeEqualToNullOrOtherType() {
        BankAccount account = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);

        assertThat(account)
            .isNotEqualTo(null)
            .isNotEqualTo(new Object());
    }
}
