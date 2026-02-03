package com.jcondotta.recipients.application.usecase.get_recipients;

import com.jcondotta.recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.recipients.application.usecase.get_recipients.model.result.GetRecipientsResult;

public interface GetRecipientsUseCase {

  GetRecipientsResult execute(GetRecipientsQuery accountRecipientsQuery);
}
