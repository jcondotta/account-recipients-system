package com.jcondotta.recipients.application.usecase.create_recipient;

import com.jcondotta.recipients.application.ports.output.facade.bank_account.BankAccountLookupFacade;
import com.jcondotta.recipients.application.ports.output.messaging.RecipientCreatedEventPublisher;
import com.jcondotta.recipients.application.ports.output.repository.create_recipient.CreateRecipientRepository;
import com.jcondotta.recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.recipients.domain.events.RecipientCreatedEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateRecipientUseCaseImpl implements CreateRecipientUseCase {

  private final BankAccountLookupFacade bankAccountLookupFacade;
  private final CreateRecipientRepository createRecipientRepository;
  private final RecipientCreatedEventPublisher eventPublisher;
  private final Clock clock;

  @Override
  @Observed(
      name = "bankAccounts.recipients.create",
      contextualName = "createRecipient",
      lowCardinalityKeyValues = {"operation", "create"})
  public void execute(CreateRecipientCommand command) {
    Objects.requireNonNull(command, "command must not be null");

    log.info("Attempting to create a recipient [bankAccountId={}, recipientName={}]",
        command.bankAccountId(),
        command.recipientName());

    var bankAccount = bankAccountLookupFacade.byId(command.bankAccountId());
    var recipient = bankAccount.createRecipient(command.recipientName(), command.iban(), clock);

    createRecipientRepository.create(recipient);

    RecipientCreatedEvent event = (RecipientCreatedEvent) bankAccount.pullRecipientEvents().getFirst();
    eventPublisher.publish(event);

    log.info(
        "Recipient created successfully [bankAccountId={}, recipientName={}]",
        command.bankAccountId(),
        command.recipientName());
  }
}
