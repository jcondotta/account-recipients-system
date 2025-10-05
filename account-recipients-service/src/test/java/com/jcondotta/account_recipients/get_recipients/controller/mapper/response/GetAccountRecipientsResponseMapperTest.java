package com.jcondotta.account_recipients.get_recipients.controller.mapper.response;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.AccountRecipientDetails;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.AccountRecipientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountRecipientsResponseMapperImplTest {

    private GetAccountRecipientsResponseMapperImpl mapper;

    private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();
    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final String RECIPIENT_NAME_VALUE = "Jefferson Condotta";
    private static final String IBAN_VALUE = "ES9820385778983000760236";

    private static final AccountRecipientId ACCOUNT_RECIPIENT_ID = AccountRecipientId.of(ACCOUNT_RECIPIENT_UUID);
    private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
    private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_VALUE);
    private static final Iban IBAN = Iban.of(IBAN_VALUE);
    private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(ClockTestFactory.TEST_CLOCK_FIXED);

    @Mock
    private AccountRecipientDetails detailsMock;

    @BeforeEach
    void setUp() {
        mapper = new GetAccountRecipientsResponseMapperImpl();
    }

    // --- toAccountRecipientResponse() ---

    @Test
    void shouldReturnNull_whenDetailsIsNull() {
        // when
        AccountRecipientResponse response = mapper.toAccountRecipientResponse(null);

        // then
        assertThat(response).isNull();
    }

    @Test
    void shouldMapAllFields_whenAllValueObjectsArePresent() {
        // given
        var details = AccountRecipientDetails.of(
            ACCOUNT_RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            CREATED_AT
        );

        // when
        AccountRecipientResponse response = mapper.toAccountRecipientResponse(details);

        // then
        assertThat(response)
            .satisfies(it -> {
                assertThat(it.accountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_UUID);
                assertThat(it.bankAccountId()).isEqualTo(BANK_ACCOUNT_UUID);
                assertThat(it.recipientName()).isEqualTo(RECIPIENT_NAME_VALUE);
                assertThat(it.iban()).isEqualTo(IBAN_VALUE);
            });
    }

    @Test
    void shouldReturnNull_whenDetailsListIsNull() {
        // when
        List<AccountRecipientResponse> result = mapper.toAccountRecipientResponses(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnEmptyList_whenDetailsListIsEmpty() {
        // when
        List<AccountRecipientResponse> result = mapper.toAccountRecipientResponses(List.of());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapListContainingMultipleValidElements() {
        // given
        var details1 = AccountRecipientDetails.of(
            ACCOUNT_RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            CREATED_AT
        );

        var details2 = AccountRecipientDetails.of(
            AccountRecipientId.of(UUID.randomUUID()),
            BankAccountId.of(UUID.randomUUID()),
            RecipientName.of("Erika Condotta"),
            Iban.of("PT50000201231234567890154"),
            CREATED_AT
        );

        var list = List.of(details1, details2);

        // when
        var result = mapper.toAccountRecipientResponses(list);

        // then
        assertThat(result)
            .hasSize(2)
            .satisfies(responses -> {
                assertThat(responses.get(0).accountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_UUID);
                assertThat(responses.get(0).recipientName()).isEqualTo(RECIPIENT_NAME_VALUE);
                assertThat(responses.get(1).recipientName()).isEqualTo("Erika Condotta");
                assertThat(responses.get(1).iban()).isEqualTo("PT50000201231234567890154");
            });
    }

    @Test
    void shouldMapListWithMixedNullAndValidDetails() {
        // given
        var accountRecipientDetails = AccountRecipientDetails.of(
            ACCOUNT_RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            RECIPIENT_NAME,
            IBAN,
            CREATED_AT
        );
        var list = List.of(accountRecipientDetails);

        var responses = mapper.toAccountRecipientResponses(list);

        // then
        assertThat(responses)
            .hasSize(1)
            .satisfies(listResult -> {
                var second = listResult.getLast();
                assertThat(second.accountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_UUID);
                assertThat(second.bankAccountId()).isEqualTo(BANK_ACCOUNT_UUID);
                assertThat(second.recipientName()).isEqualTo(RECIPIENT_NAME_VALUE);
                assertThat(second.iban()).isEqualTo(IBAN_VALUE);
            });
    }

    @Test
    void shouldReturnResponseWithAllNullFields_whenAllValueObjectsAreNullInsideDetails() {
        // given
        when(detailsMock.accountRecipientId()).thenReturn(null);
        when(detailsMock.bankAccountId()).thenReturn(null);
        when(detailsMock.recipientName()).thenReturn(null);
        when(detailsMock.iban()).thenReturn(null);

        // when
        AccountRecipientResponse response = mapper.toAccountRecipientResponse(detailsMock);

        // then
        verify(detailsMock).accountRecipientId();
        verify(detailsMock).bankAccountId();
        verify(detailsMock).recipientName();
        verify(detailsMock).iban();

        assertThat(response)
            .satisfies(it -> {
                assertThat(it.accountRecipientId()).isNull();
                assertThat(it.bankAccountId()).isNull();
                assertThat(it.recipientName()).isNull();
                assertThat(it.iban()).isNull();
            });
    }
}
