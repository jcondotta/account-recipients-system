package com.jcondotta.account_recipients.application.usecase.get_recipients;

import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;

public interface GetAccountRecipientsUseCase {

    GetAccountRecipientsResult execute(GetAccountRecipientsQuery accountRecipientsQuery);
}
