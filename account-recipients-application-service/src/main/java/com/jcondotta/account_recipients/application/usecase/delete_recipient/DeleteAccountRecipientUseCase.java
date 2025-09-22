package com.jcondotta.account_recipients.application.usecase.delete_recipient;

import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;

public interface DeleteAccountRecipientUseCase {

    void execute(DeleteAccountRecipientCommand command);
}
