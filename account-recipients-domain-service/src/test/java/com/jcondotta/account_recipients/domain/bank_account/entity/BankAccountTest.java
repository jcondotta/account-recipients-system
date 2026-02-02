package com.jcondotta.account_recipients.domain.bank_account.entity;

import com.jcondotta.account_recipients.domain.bank_account.enums.AccountStatus;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientEvent;
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
import java.util.List;
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
    private static final Clock DELETED_CLOCK = Clock.fixed(Instant.parse("2023-12-15T12:50:58Z"), ZoneOffset.UTC);

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
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
        var recipient = bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);

        assertThat(recipient.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(recipient.getRecipientId()).isNotNull();
        assertThat(recipient.getRecipientName()).isEqualTo(RECIPIENT_NAME_JEFFERSON);
        assertThat(recipient.getIban()).isEqualTo(IBAN);
        assertThat(recipient.getCreatedAt()).isEqualTo(ZonedDateTime.now(CLOCK));
        assertThat(recipient.getDeletedAt()).isNull();
        assertThat(recipient.isDeleted()).isFalse();

        assertThat(bankAccount.pullRecipientEvents())
            .singleElement()
            .isInstanceOfSatisfying(RecipientCreatedEvent.class, event -> {
                assertThat(event.recipientId()).isEqualTo(recipient.getRecipientId());
                assertThat(event.recipientName()).isEqualTo(recipient.getRecipientName());
                assertThat(event.bankAccountId()).isEqualTo(recipient.getBankAccountId());
                assertThat(event.iban()).isEqualTo(recipient.getIban());
                assertThat(event.occurredAt()).isEqualTo(recipient.getCreatedAt());
            });

    }

    @ParameterizedTest
    @EnumSource(value = AccountStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"ACTIVE"})
    void shouldThrowIllegalStateException_whenAccountIsNotActive(AccountStatus accountStatus) {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, accountStatus);

        assertThatThrownBy(() -> bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot create recipient for non-active account");
    }

    @Test
    void shouldDeleteRecipient_whenAccountIsActiveAndRecipientBelongsToAccount() {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
        var recipient = AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);

        bankAccount.deleteRecipient(recipient, DELETED_CLOCK);

        assertThat(recipient.isDeleted()).isTrue();
        assertThat(recipient.getDeletedAt()).isEqualTo(ZonedDateTime.now(DELETED_CLOCK));

        assertThat(bankAccount.pullRecipientEvents())
            .singleElement()
            .isInstanceOfSatisfying(RecipientDeletedEvent.class, event -> {
                assertThat(event.recipientId()).isEqualTo(recipient.getRecipientId());
                assertThat(event.bankAccountId()).isEqualTo(recipient.getBankAccountId());
                assertThat(event.occurredAt()).isEqualTo(recipient.getDeletedAt());
            });
    }

    @Test
    void shouldThrowIllegalStateException_whenRecipientDoesNotBelongToAccount() {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
        var recipient = AccountRecipient.create(
            BankAccountId.of(UUID.randomUUID()),
            RECIPIENT_NAME_JEFFERSON,
            IBAN,
            CLOCK
        );

        assertThatThrownBy(() -> bankAccount.deleteRecipient(recipient, CLOCK))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Recipient does not belong to this account");
    }

    @ParameterizedTest
    @EnumSource(value = AccountStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
    void shouldThrowIllegalStateException_whenDeleteRecipientWhichAccountIsNotActive(AccountStatus status) {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, status);
        var recipient = AccountRecipient.create(BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);

        assertThatThrownBy(() -> bankAccount.deleteRecipient(recipient, CLOCK))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot delete recipient for non-active account");
    }

    @Test
    void shouldPullRecipientEvents_andClearInternalList() {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);

        bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);
        bankAccount.createRecipient(RecipientName.of("Another Recipient"), IBAN, CLOCK);

        var events = bankAccount.pullRecipientEvents();
        assertThat(events).hasSize(2);

        assertThat(bankAccount.pullRecipientEvents()).isEmpty();
    }

    @Test
    void shouldReturnImmutableList_whenPullingRecipientEvents() {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
        bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN, CLOCK);

        List<RecipientEvent> events = bankAccount.pullRecipientEvents();

        assertThatThrownBy(() -> events.add(null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldBeEqual_whenBankAccountIdIsSame() {
        var bankAccount1 = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
        var bankAccount2 = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.CANCELLED);

        assertThat(bankAccount1)
            .isEqualTo(bankAccount2)
            .hasSameHashCodeAs(bankAccount2);
    }

    @Test
    void shouldNotBeEqual_whenBankAccountIdIsDifferent() {
        var bankAccount1 = BankAccount.restore(BankAccountId.of(UUID.randomUUID()), AccountStatus.ACTIVE);
        var bankAccount2 = BankAccount.restore(BankAccountId.of(UUID.randomUUID()), AccountStatus.ACTIVE);

        assertThat(bankAccount1).isNotEqualTo(bankAccount2);
    }

    @Test
    void shouldBeEqualToItself() {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);
        assertThat(bankAccount).isEqualTo(bankAccount);
    }

    @Test
    void shouldNotBeEqualToNullOrOtherType() {
        var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE);

        assertThat(bankAccount)
            .isNotEqualTo(null)
            .isNotEqualTo(new Object());
    }
}
