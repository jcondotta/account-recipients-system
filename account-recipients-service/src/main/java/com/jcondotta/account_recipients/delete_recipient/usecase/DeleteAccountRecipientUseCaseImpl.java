package com.jcondotta.account_recipients.delete_recipient.usecase;

import com.jcondotta.account_recipients.application.ports.output.facade.lookup_bank_account.LookupBankAccountFacade;
import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientDeletedEventPublisher;
import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipient.GetAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.DeleteAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.domain.bank_account.entity.BankAccount;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.recipient.exceptions.AccountRecipientNotFoundException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteAccountRecipientUseCaseImpl implements DeleteAccountRecipientUseCase {

  private final LookupBankAccountFacade lookupBankAccountFacade;
  private final GetAccountRecipientRepository getAccountRecipientRepository;
  private final DeleteAccountRecipientRepository deleteAccountRecipientRepository;
  private final RecipientDeletedEventPublisher eventPublisher;
  private final Clock clock;

  @Override
  @Observed(
      name = "account.recipients.delete",
      contextualName = "deleteAccountRecipient",
      lowCardinalityKeyValues = {"operation", "delete"})
  public void execute(DeleteAccountRecipientCommand command, IdempotencyKey idempotencyKey) {
    Objects.requireNonNull(command, "Command must not be null");

    log.info(
        "Attempting to delete a recipient [bankAccountId={}, recipientId={}]",
        command.bankAccountId(),
        command.recipientId());

    BankAccount bankAccount = lookupBankAccountFacade.byId(command.bankAccountId());
    var accountRecipient = getAccountRecipientRepository.getAccountRecipient(command.bankAccountId(), command.recipientId())
        .orElseThrow(() -> new AccountRecipientNotFoundException(command.bankAccountId(), command.recipientId()));

    bankAccount.deleteRecipient(accountRecipient, clock);
    deleteAccountRecipientRepository.delete(accountRecipient);

    RecipientDeletedEvent event = (RecipientDeletedEvent) bankAccount.pullRecipientEvents().getFirst();
    eventPublisher.send(event, idempotencyKey);

    log.info(
        "Recipient deleted successfully [bankAccountId={}, recipientId={}]",
        command.bankAccountId(),
        command.recipientId()
    );
  }
}
