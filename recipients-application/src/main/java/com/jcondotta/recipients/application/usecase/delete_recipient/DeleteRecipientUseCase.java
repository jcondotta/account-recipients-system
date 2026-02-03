package com.jcondotta.recipients.application.usecase.delete_recipient;

import com.jcondotta.recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;
import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;

public interface DeleteRecipientUseCase {

  void execute(DeleteRecipientCommand command, IdempotencyKey idempotencyKey);
}
