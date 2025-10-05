package com.jcondotta.account_recipients.create_recipient.controller.mapper;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.create_recipient.controller.model.CreateAccountRecipientRestRequest;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link CreateAccountRecipientRequestRestMapper}.
 */
class CreateAccountRecipientRequestRestMapperTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);

    private static final String RECIPIENT_NAME_STRING = "Jefferson Condotta";
    private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_STRING);

    private static final String VALID_IBAN = "ES9820385778983000760236";
    private static final Iban IBAN = Iban.of(VALID_IBAN);

    private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;
    private static final ZonedDateTime FIXED_NOW = ZonedDateTime.now(FIXED_CLOCK);

    private final  CreateAccountRecipientRequestRestMapper mapper = CreateAccountRecipientRequestRestMapper.INSTANCE;

    @Test
    void shouldMapToCommand_whenRequestIsValid() {
        var request = new CreateAccountRecipientRestRequest(RECIPIENT_NAME_STRING, VALID_IBAN);

        CreateAccountRecipientCommand command = mapper.toCommand(BANK_ACCOUNT_UUID, request, FIXED_CLOCK);

        assertThat(command)
            .satisfies(it -> {
                assertThat(it.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(it.recipientName()).isEqualTo(RECIPIENT_NAME);
                assertThat(it.iban()).isEqualTo(IBAN);
                assertThat(it.createdAt()).isEqualTo(FIXED_NOW);
            });
    }

    @Test
    void shouldReturnNull_whenBankAccountIdAndRequestAreNull() {
        var result = mapper.toCommand(null, null, FIXED_CLOCK);
        assertThat(result).isNull();
    }

    @Test
    void shouldThrowException_whenBankAccountIdIsNullButRequestIsNot() {
        var request = new CreateAccountRecipientRestRequest(RECIPIENT_NAME_STRING, VALID_IBAN);

        assertThatThrownBy(() -> mapper.toCommand(null, request, FIXED_CLOCK))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowException_whenRequestIsNullButBankAccountIdIsNot() {
        assertThatThrownBy(() -> mapper.toCommand(BANK_ACCOUNT_UUID, null, FIXED_CLOCK))
            .isInstanceOf(NullPointerException.class);
    }
}
