package com.jcondotta.account_recipients.application.usecase.delete_recipient;

import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;

public interface DeleteRecipientUseCase {

  void execute(DeleteRecipientCommand command, IdempotencyKey idempotencyKey);
}
