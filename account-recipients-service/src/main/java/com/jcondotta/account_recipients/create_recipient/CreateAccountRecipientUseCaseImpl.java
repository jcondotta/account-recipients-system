package com.jcondotta.account_recipients.create_recipient;

import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.IdempotencyKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateAccountRecipientUseCaseImpl implements CreateAccountRecipientUseCase {

    @Override
    public void execute(CreateAccountRecipientCommand command, IdempotencyKey idempotencyKey) {
        log.info(command.toString());
    }
}
