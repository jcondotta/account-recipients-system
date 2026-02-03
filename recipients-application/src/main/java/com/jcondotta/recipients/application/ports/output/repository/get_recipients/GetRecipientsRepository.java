package com.jcondotta.recipients.application.ports.output.repository.get_recipients;

import com.jcondotta.recipients.application.ports.output.repository.shared.model.PaginatedResult;
import com.jcondotta.recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.recipients.domain.entities.Recipient;

public interface GetRecipientsRepository {

  PaginatedResult<Recipient> findByQuery(GetRecipientsQuery query);
}
