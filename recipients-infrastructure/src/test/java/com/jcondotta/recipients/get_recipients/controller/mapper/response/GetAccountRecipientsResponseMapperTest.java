package com.jcondotta.recipients.get_recipients.controller.mapper.response;

import com.jcondotta.recipients.application.usecase.get_recipients.model.RecipientDetails;
import com.jcondotta.recipients.common.factory.ClockTestFactory;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.get_recipients.controller.model.response.RecipientResponse;
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
class GetRecipientsResponseMapperImplTest {

  private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();
  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final String RECIPIENT_NAME_VALUE = "Jefferson Condotta";
  private static final String IBAN_VALUE = "ES9820385778983000760236";
  private static final RecipientId ACCOUNT_RECIPIENT_ID =
      RecipientId.of(ACCOUNT_RECIPIENT_UUID);
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(RECIPIENT_NAME_VALUE);
  private static final Iban IBAN = Iban.of(IBAN_VALUE);
  private static final ZonedDateTime CREATED_AT =
      ZonedDateTime.now(ClockTestFactory.TEST_CLOCK_FIXED);
  private GetAccountRecipientsResponseMapperImpl mapper;
  @Mock
  private RecipientDetails detailsMock;

  @BeforeEach
  void setUp() {
    mapper = new GetAccountRecipientsResponseMapperImpl();
  }

  // --- toAccountRecipientResponse() ---

  @Test
  void shouldReturnNull_whenDetailsIsNull() {
    // when
    RecipientResponse response = mapper.toAccountRecipientResponse(null);

    // then
    assertThat(response).isNull();
  }

  @Test
  void shouldMapAllFields_whenAllValueObjectsArePresent() {
    // given
    var details =
        RecipientDetails.of(
            ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);

    // when
    RecipientResponse response = mapper.toAccountRecipientResponse(details);

    // then
    assertThat(response)
        .satisfies(
            it -> {
              assertThat(it.recipientId()).isEqualTo(ACCOUNT_RECIPIENT_UUID);
              assertThat(it.bankAccountId()).isEqualTo(BANK_ACCOUNT_UUID);
              assertThat(it.recipientName()).isEqualTo(RECIPIENT_NAME_VALUE);
              assertThat(it.iban()).isEqualTo(IBAN_VALUE);
            });
  }

  @Test
  void shouldReturnNull_whenDetailsListIsNull() {
    // when
    List<RecipientResponse> result = mapper.toAccountRecipientResponses(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  void shouldReturnEmptyList_whenDetailsListIsEmpty() {
    // when
    List<RecipientResponse> result = mapper.toAccountRecipientResponses(List.of());

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldMapListContainingMultipleValidElements() {
    // given
    var details1 =
        RecipientDetails.of(
            ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);

    var details2 =
        RecipientDetails.of(
            RecipientId.of(UUID.randomUUID()),
            BankAccountId.of(UUID.randomUUID()),
            RecipientName.of("Erika Condotta"),
            Iban.of("PT50000201231234567890154"),
            CREATED_AT);

    var list = List.of(details1, details2);

    // when
    var result = mapper.toAccountRecipientResponses(list);

    // then
    assertThat(result)
        .hasSize(2)
        .satisfies(
            responses -> {
              assertThat(responses.get(0).recipientId()).isEqualTo(ACCOUNT_RECIPIENT_UUID);
              assertThat(responses.get(0).recipientName()).isEqualTo(RECIPIENT_NAME_VALUE);
              assertThat(responses.get(1).recipientName()).isEqualTo("Erika Condotta");
              assertThat(responses.get(1).iban()).isEqualTo("PT50000201231234567890154");
            });
  }

  @Test
  void shouldMapListWithMixedNullAndValidDetails() {
    // given
    var accountRecipientDetails =
        RecipientDetails.of(
            ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT);
    var list = List.of(accountRecipientDetails);

    var responses = mapper.toAccountRecipientResponses(list);

    // then
    assertThat(responses)
        .hasSize(1)
        .satisfies(
            listResult -> {
              var second = listResult.getLast();
              assertThat(second.recipientId()).isEqualTo(ACCOUNT_RECIPIENT_UUID);
              assertThat(second.bankAccountId()).isEqualTo(BANK_ACCOUNT_UUID);
              assertThat(second.recipientName()).isEqualTo(RECIPIENT_NAME_VALUE);
              assertThat(second.iban()).isEqualTo(IBAN_VALUE);
            });
  }

  @Test
  void shouldReturnResponseWithAllNullFields_whenAllValueObjectsAreNullInsideDetails() {
    // given
    when(detailsMock.recipientId()).thenReturn(null);
    when(detailsMock.bankAccountId()).thenReturn(null);
    when(detailsMock.recipientName()).thenReturn(null);
    when(detailsMock.iban()).thenReturn(null);

    // when
    RecipientResponse response = mapper.toAccountRecipientResponse(detailsMock);

    // then
    verify(detailsMock).recipientId();
    verify(detailsMock).bankAccountId();
    verify(detailsMock).recipientName();
    verify(detailsMock).iban();

    assertThat(response)
        .satisfies(
            it -> {
              assertThat(it.recipientId()).isNull();
              assertThat(it.bankAccountId()).isNull();
              assertThat(it.recipientName()).isNull();
              assertThat(it.iban()).isNull();
            });
  }
}
