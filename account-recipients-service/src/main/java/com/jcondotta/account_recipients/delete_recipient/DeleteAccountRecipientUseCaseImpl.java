package com.jcondotta.account_recipients.delete_recipient;

import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.DeleteAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteAccountRecipientUseCaseImpl implements DeleteAccountRecipientUseCase {

    private final DeleteAccountRecipientRepository repository;

    @Override
    public void execute(DeleteAccountRecipientCommand command) {
        log.info("Attempting to delete a recipient [bankAccountId={}, accountRecipientId={}]",
            command.bankAccountId(), command.accountRecipientId());

        repository.delete(command.bankAccountId(), command.accountRecipientId());
    }
}