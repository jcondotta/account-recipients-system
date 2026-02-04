package com.jcondotta.recipients.application.usecase.delete_recipient;

import com.jcondotta.recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;

public interface DeleteRecipientUseCase {

  void execute(DeleteRecipientCommand command);
}
