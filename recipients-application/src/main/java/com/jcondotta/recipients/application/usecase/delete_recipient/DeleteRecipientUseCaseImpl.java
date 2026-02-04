package com.jcondotta.recipients.application.usecase.delete_recipient;

import com.jcondotta.recipients.application.ports.output.facade.bank_account.BankAccountLookupFacade;
import com.jcondotta.recipients.application.ports.output.repository.delete_recipient.DeleteRecipientRepository;
import com.jcondotta.recipients.application.ports.output.repository.get_recipient.GetRecipientRepository;
import com.jcondotta.recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;
import com.jcondotta.recipients.domain.entities.BankAccount;
import com.jcondotta.recipients.domain.exceptions.RecipientNotFoundException;
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
  private final Clock clock;

  @Override
  @Observed(
      name = "bankAccounts.recipients.delete",
      contextualName = "deleteRecipient",
      lowCardinalityKeyValues = {"operation", "delete"})
  public void execute(DeleteRecipientCommand command) {
    Objects.requireNonNull(command, "command must not be null");

    log.info(
        "Attempting to delete a recipient [bankAccountId={}, recipientId={}]",
        command.bankAccountId(),
        command.recipientId());

    BankAccount bankAccount = bankAccountLookupFacade.byId(command.bankAccountId());
    var recipient = getRecipientRepository.getRecipient(command.bankAccountId(), command.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(command.bankAccountId(), command.recipientId()));

    bankAccount.deleteRecipient(recipient, clock);
    deleteRecipientRepository.delete(recipient);

    log.info(
        "Recipient deleted successfully [bankAccountId={}, recipientId={}]",
        command.bankAccountId(),
        command.recipientId()
    );
  }
}
