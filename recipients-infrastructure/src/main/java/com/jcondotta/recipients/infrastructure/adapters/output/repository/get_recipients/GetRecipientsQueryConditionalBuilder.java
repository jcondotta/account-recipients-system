package com.jcondotta.recipients.infrastructure.adapters.output.repository.get_recipients;

import com.jcondotta.recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntityKey;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Objects;

public final class GetRecipientsQueryConditionalBuilder {

  private GetRecipientsQueryConditionalBuilder() {}

  public static QueryConditional build(GetRecipientsQuery query) {
    var partitionKey = RecipientEntityKey.partitionKey(query.bankAccountId());
    var queryParams = query.queryParams();

    if (Objects.nonNull(queryParams.namePrefix())) {
      return QueryConditional.sortBeginsWith(
          k -> k.partitionValue(partitionKey)
                .sortValue(queryParams.namePrefix().value()));
    }

    return QueryConditional.keyEqualTo(
        k -> k.partitionValue(partitionKey));
  }
}
