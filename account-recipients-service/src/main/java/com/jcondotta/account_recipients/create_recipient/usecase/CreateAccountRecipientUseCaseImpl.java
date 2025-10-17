package com.jcondotta.account_recipients.create_recipient.usecase;

import com.jcondotta.account_recipients.application.ports.output.facade.lookup_bank_account.LookupBankAccountFacade;
import com.jcondotta.account_recipients.application.ports.output.repository.create_recipient.CreateAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.create_recipient.mapper.CreateAccountRecipientCommandMapper;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.IdempotencyKey;
import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateAccountRecipientUseCaseImpl implements CreateAccountRecipientUseCase {

    private final LookupBankAccountFacade lookupBankAccountFacade;
    private final CreateAccountRecipientCommandMapper commandMapper;
    private final CreateAccountRecipientRepository createAccountRecipientRepository;

    @Override
    @Observed(
        name = "account_recipients.create",
        contextualName = "createAccountRecipient",
        lowCardinalityKeyValues = {"module", "account-recipients", "operation", "create"}
    )
    public void execute(CreateAccountRecipientCommand command, IdempotencyKey idempotencyKey) {
        log.info("Attempting to create a recipient [bankAccountId={}, accountRecipientId={}]",
            command.bankAccountId(), command.recipientName());

        lookupBankAccountFacade.byId(command.bankAccountId());
        createAccountRecipientRepository.create(commandMapper.toAccountRecipient(command));

        log.info("Recipient created successfully [bankAccountId={}, accountRecipientId={}]",
            command.bankAccountId(), command.recipientName());
    }
}
