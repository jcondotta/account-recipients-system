package com.jcondotta.recipients.application.usecase.create_recipient;

import com.jcondotta.recipients.application.usecase.create_recipient.model.CreateRecipientCommand;

public interface CreateRecipientUseCase {

  void execute(CreateRecipientCommand command);
}
