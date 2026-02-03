package com.jcondotta.account_recipients.delete_recipient.usecase;

import com.jcondotta.account_recipients.application.ports.output.facade.bank_account.BankAccountLookupFacade;
import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientDeletedEventPublisher;
import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteRecipientRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipient.GetRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.DeleteRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.domain.entities.BankAccount;
import com.jcondotta.account_recipients.domain.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.exceptions.RecipientNotFoundException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteRecipientUseCaseImpl implements DeleteRecipientUseCase {

  private final BankAccountLookupFacade bankAccountLookupFacade;
  private final GetRecipientRepository getRecipientRepository;
  private final DeleteRecipientRepository deleteRecipientRepository;
  private final RecipientDeletedEventPublisher eventPublisher;
  private final Clock clock;

  @Override
  @Observed(
      name = "account.recipients.delete",
      contextualName = "deleteAccountRecipient",
      lowCardinalityKeyValues = {"operation", "delete"})
  public void execute(DeleteRecipientCommand command, IdempotencyKey idempotencyKey) {
    Objects.requireNonNull(command, "Command must not be null");

    log.info(
        "Attempting to delete a recipient [bankAccountId={}, recipientId={}]",
        command.bankAccountId(),
        command.recipientId());

    BankAccount bankAccount = bankAccountLookupFacade.byId(command.bankAccountId());
    var recipient = getRecipientRepository.getRecipient(command.bankAccountId(), command.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(command.bankAccountId(), command.recipientId()));

    bankAccount.deleteRecipient(recipient, clock);
    deleteRecipientRepository.delete(recipient);

    RecipientDeletedEvent event = (RecipientDeletedEvent) bankAccount.pullRecipientEvents().getFirst();
    eventPublisher.send(event, idempotencyKey);

    log.info(
        "Recipient deleted successfully [bankAccountId={}, recipientId={}]",
        command.bankAccountId(),
        command.recipientId()
    );
  }
}
