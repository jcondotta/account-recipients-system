package com.jcondotta.account_recipients.application.usecase.create_recipient;

import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;

public interface CreateRecipientUseCase {

  void execute(CreateRecipientCommand command, IdempotencyKey idempotencyKey);
}
