package com.jcondotta.account_recipients.application.usecase.create_recipient;

import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.IdempotencyKey;

public interface CreateAccountRecipientUseCase {

    void execute(CreateAccountRecipientCommand command, IdempotencyKey idempotencyKey);
}
