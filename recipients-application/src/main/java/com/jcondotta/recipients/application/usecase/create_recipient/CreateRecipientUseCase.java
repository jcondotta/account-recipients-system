package com.jcondotta.recipients.application.usecase.create_recipient;

import com.jcondotta.recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;

public interface CreateRecipientUseCase {

  void execute(CreateRecipientCommand command, IdempotencyKey idempotencyKey);
}
