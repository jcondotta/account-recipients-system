package com.jcondotta.account_recipients.delete_recipient.usecase;

import com.jcondotta.account_recipients.application.events.mapper.RecipientDeletedEventMapper;
import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientDeletedEventPublisher;
import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipient.GetAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.DeleteAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.delete_recipient.model.DeleteAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
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

  private final GetAccountRecipientRepository getAccountRecipientRepository;
  private final DeleteAccountRecipientRepository deleteAccountRecipientRepository;
  private final RecipientDeletedEventPublisher deletedEventPublisher;
  private final RecipientDeletedEventMapper eventMapper;
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

    var accountRecipient = getAccountRecipientRepository.getAccountRecipient(command.bankAccountId(), command.recipientId())
        .orElseThrow(() -> new AccountRecipientNotFoundException(command.bankAccountId(), command.recipientId()));

    accountRecipient.delete(clock);

    deletedEventPublisher.send(eventMapper.fromAccountRecipient(accountRecipient), idempotencyKey);
    deleteAccountRecipientRepository.delete(accountRecipient);
  }
}
