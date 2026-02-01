package com.jcondotta.account_recipients.create_recipient.usecase;

import com.jcondotta.account_recipients.application.events.mapper.RecipientCreatedEventMapper;
import com.jcondotta.account_recipients.application.ports.output.facade.lookup_bank_account.LookupBankAccountFacade;
import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientCreatedEventPublisher;
import com.jcondotta.account_recipients.application.ports.output.repository.create_recipient.CreateAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateAccountRecipientUseCaseImpl implements CreateAccountRecipientUseCase {

  private final LookupBankAccountFacade lookupBankAccountFacade;
  private final CreateAccountRecipientRepository createAccountRecipientRepository;
  private final RecipientCreatedEventPublisher eventPublisher;
  private final RecipientCreatedEventMapper recipientCreatedEventMapper;
  private final Clock clock;

  @Override
  @Observed(
      name = "account.recipients.create",
      contextualName = "createAccountRecipient",
      lowCardinalityKeyValues = {"operation", "create"})
  public void execute(CreateAccountRecipientCommand command, IdempotencyKey idempotencyKey) {
    Objects.requireNonNull(command, "command must not be null");
    Objects.requireNonNull(idempotencyKey, "idempotencyKey must not be null");

    log.info("Attempting to create a recipient [bankAccountId={}, recipientName={}]",
        command.bankAccountId(),
        command.recipientName());

    BankAccount bankAccount = lookupBankAccountFacade.byId(command.bankAccountId());
    AccountRecipient accountRecipient = bankAccount.createRecipient(command.recipientName(), command.iban(), clock);

    createAccountRecipientRepository.create(accountRecipient);

    var recipientCreatedEvent = recipientCreatedEventMapper.fromAccountRecipient(accountRecipient);
    eventPublisher.send(recipientCreatedEvent, idempotencyKey);

    log.info(
        "Recipient created successfully [bankAccountId={}, recipientName={}]",
        command.bankAccountId(),
        command.recipientName());
  }
}
