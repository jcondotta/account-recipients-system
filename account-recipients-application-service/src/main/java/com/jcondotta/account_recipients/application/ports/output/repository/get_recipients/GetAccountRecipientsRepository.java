package com.jcondotta.account_recipients.application.ports.output.repository.get_recipients;

import com.jcondotta.account_recipients.application.ports.output.repository.shared.model.PaginatedResult;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;

public interface GetAccountRecipientsRepository {

    PaginatedResult<AccountRecipient> findByQuery(GetAccountRecipientsQuery query);
}
