package com.jcondotta.recipients.infrastructure.adapters.output.repository.get_recipients;

import com.jcondotta.recipients.application.ports.output.repository.get_recipients.model.GetRecipientsQueryParams;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.recipients.application.ports.output.repository.shared.value_objects.RecipientNamePrefix;
import com.jcondotta.recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.internal.conditional.BeginsWithConditional;
import software.amazon.awssdk.enhanced.dynamodb.internal.conditional.EqualToConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetRecipientsQueryConditionalBuilderTest {

  @Test
  void shouldBuildKeyEqualTo_whenNamePrefixIsNull() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    var params = GetRecipientsQueryParams.of(QueryLimit.of(10));
    var query = GetRecipientsQuery.of(bankAccountId, params);

    QueryConditional conditional =
        GetRecipientsQueryConditionalBuilder.build(query);

    assertThat(conditional)
        .isInstanceOf(EqualToConditional.class);
  }

  @Test
  void shouldBuildSortBeginsWith_whenNamePrefixIsPresent() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    var params =
        GetRecipientsQueryParams.of(
            QueryLimit.of(10),
            RecipientNamePrefix.of("Je"),
            null);

    var query = GetRecipientsQuery.of(bankAccountId, params);

    QueryConditional conditional = GetRecipientsQueryConditionalBuilder.build(query);

    assertThat(conditional)
        .isInstanceOf(BeginsWithConditional.class);
  }
}