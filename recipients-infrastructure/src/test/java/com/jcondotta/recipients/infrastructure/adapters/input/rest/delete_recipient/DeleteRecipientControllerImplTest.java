package com.jcondotta.recipients.infrastructure.adapters.input.rest.delete_recipient;

import com.jcondotta.recipients.application.usecase.delete_recipient.DeleteRecipientUseCase;
import com.jcondotta.recipients.application.usecase.delete_recipient.model.DeleteRecipientCommand;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipientControllerImplTest {

  private static final UUID IDEMPOTENCY_KEY_UUID = UUID.randomUUID();
  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID RECIPIENT_UUID = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
  private static final RecipientId RECIPIENT_ID = RecipientId.of(RECIPIENT_UUID);

  @Mock
  private DeleteRecipientRequestMapper requestMapper;

  @Mock
  private DeleteRecipientUseCase useCase;

  @Captor
  private ArgumentCaptor<DeleteRecipientCommand> commandCaptor;

  private DeleteRecipientControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new DeleteRecipientControllerImpl(requestMapper, useCase);
  }

  @Test
  void shouldReturn204NoContent_whenRecipientIsDeleted() {
    DeleteRecipientCommand command =
        DeleteRecipientCommand.of(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(requestMapper.toCommand(BANK_ACCOUNT_UUID, RECIPIENT_UUID))
        .thenReturn(command);

    ResponseEntity<Void> response =
        controller.deleteAccountRecipient(
            IDEMPOTENCY_KEY_UUID,
            BANK_ACCOUNT_UUID,
            RECIPIENT_UUID
        );

    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();

    verify(requestMapper).toCommand(BANK_ACCOUNT_UUID, RECIPIENT_UUID);
    verify(useCase).execute(commandCaptor.capture());

    DeleteRecipientCommand capturedCommand = commandCaptor.getValue();
    assertThat(capturedCommand.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(capturedCommand.recipientId()).isEqualTo(RECIPIENT_ID);

    verifyNoMoreInteractions(requestMapper, useCase);
  }
}
