package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients;

import com.jcondotta.account_recipients.application.ports.output.repository.shared.model.PaginatedResult;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.account_recipients.domain.entities.Recipient;

public interface GetRecipientsRepository {

  PaginatedResult<Recipient> findByQuery(GetRecipientsQuery query);
}
