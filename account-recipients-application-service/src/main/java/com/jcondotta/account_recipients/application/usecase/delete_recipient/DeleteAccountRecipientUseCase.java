package com.jcondotta.account_recipients.application.usecase.delete_recipient;

import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;

public interface DeleteAccountRecipientUseCase {

  void execute(DeleteAccountRecipientCommand command, IdempotencyKey idempotencyKey);
}
