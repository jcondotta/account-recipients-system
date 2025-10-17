package com.jcondotta.account_recipients.delete_recipient.usecase;

import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.DeleteAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteAccountRecipientUseCaseImpl implements DeleteAccountRecipientUseCase {

    private final DeleteAccountRecipientRepository repository;

    @Override
    @Observed(
        name = "account_recipients.delete",
        contextualName = "deleteAccountRecipient",
        lowCardinalityKeyValues = {"module", "account-recipients", "operation", "delete"}
    )
    public void execute(DeleteAccountRecipientCommand command) {
        Objects.requireNonNull(command, "Command must not be null");

        log.info("Attempting to delete a recipient [bankAccountId={}, accountRecipientId={}]",
            command.bankAccountId(), command.accountRecipientId());

        repository.delete(command.bankAccountId(), command.accountRecipientId());
    }
}