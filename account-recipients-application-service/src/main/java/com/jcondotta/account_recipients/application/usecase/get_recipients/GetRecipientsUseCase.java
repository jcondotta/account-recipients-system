package com.jcondotta.account_recipients.application.usecase.get_recipients;

import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetRecipientsResult;

public interface GetRecipientsUseCase {

  GetRecipientsResult execute(GetRecipientsQuery accountRecipientsQuery);
}
