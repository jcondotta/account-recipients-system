package com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id;

import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model.GetAccountRecipientsResult;

public interface GetAccountRecipientsUseCase {

    GetAccountRecipientsResult execute(GetAccountRecipientsQuery accountRecipientsQuery);
}
