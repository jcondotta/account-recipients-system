package com.jcondotta.recipients.get_recipients.controller.model.response;

import com.jcondotta.recipients.common.fixtures.RecipientFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetRecipientsResponseTest {

  private static final UUID ACCOUNT_RECIPIENT_ID = UUID.randomUUID();
  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
  private static final String RECIPIENT_NAME =
      RecipientFixtures.JEFFERSON.getRecipientName();
  private static final String IBAN = RecipientFixtures.JEFFERSON.getRecipientIban();
  private static final String NEXT_CURSOR = "cursor-123";

  private static final RecipientResponse ACCOUNT_RECIPIENT_RESPONSE =
      RecipientResponse.of(ACCOUNT_RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN);

  @Test
  void shouldCreateResponse_whenAllFieldsAreProvided() {
    var response =
        GetRecipientsResponse.of(List.of(ACCOUNT_RECIPIENT_RESPONSE), NEXT_CURSOR);

    assertThat(response)
        .satisfies(
            it -> {
              assertThat(it.recipients())
                  .hasSize(1)
                  .first()
                  .satisfies(
                      recipient -> {
                        assertThat(recipient.recipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
                        assertThat(recipient.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                        assertThat(recipient.recipientName()).isEqualTo(RECIPIENT_NAME);
                        assertThat(recipient.iban()).isEqualTo(IBAN);
                      });
              assertThat(it.nextCursor()).isEqualTo(NEXT_CURSOR);
            });
  }

  @Test
  void shouldHaveValueEquality_whenFieldsAreIdentical() {
    var list = List.of(ACCOUNT_RECIPIENT_RESPONSE);
    var response1 = GetRecipientsResponse.of(list, NEXT_CURSOR);
    var response2 = GetRecipientsResponse.of(list, NEXT_CURSOR);

    assertThat(response1).isEqualTo(response2).hasSameHashCodeAs(response2);
  }
}
