package com.jcondotta.account_recipients.create_recipient.controller;

import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.create_recipient.controller.mapper.CreateRecipientRequestRestMapper;
import com.jcondotta.account_recipients.create_recipient.controller.model.CreateRecipientRestRequest;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientURIProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRecipientControllerImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID IDEMPOTENCY_KEY_UUID = UUID.randomUUID();

  private static final String RECIPIENT_NAME = AccountRecipientFixtures.JEFFERSON.getRecipientName();
  private static final String IBAN = AccountRecipientFixtures.JEFFERSON.getRecipientIban();

  private static final URI EXPECTED_LOCATION_URI =
      URI.create("https://api.jcondotta.com/v1/bank-accounts/" + BANK_ACCOUNT_UUID + "/account-recipients");

  @Mock
  private CreateRecipientCommand createRecipientCommand;

  @Mock
  private CreateRecipientUseCase useCase;

  @Mock
  private CreateRecipientRequestRestMapper requestMapper;

  @Mock
  private AccountRecipientURIProperties uriProperties;

  @Captor
  private ArgumentCaptor<CreateRecipientCommand> commandCaptor;

  private CreateRecipientControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller =
        new CreateRecipientControllerImpl(useCase, requestMapper, uriProperties);
  }

  @Test
  void shouldCreateAccountRecipientAndReturnCreatedResponse_whenRequestIsValid() {
    var request = CreateRecipientRestRequest.of(RECIPIENT_NAME, IBAN);

    when(requestMapper.toCommand(BANK_ACCOUNT_UUID, request))
        .thenReturn(createRecipientCommand);

    when(uriProperties.accountRecipientsURI(BANK_ACCOUNT_UUID)).thenReturn(EXPECTED_LOCATION_URI);

    ResponseEntity<String> response =
        controller.createAccountRecipient(IDEMPOTENCY_KEY_UUID, BANK_ACCOUNT_UUID, request);

    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getHeaders().getLocation()).isEqualTo(EXPECTED_LOCATION_URI);
    assertThat(response.getBody()).isNull();

    verify(requestMapper).toCommand(BANK_ACCOUNT_UUID, request);
    verify(useCase).execute(createRecipientCommand, IdempotencyKey.of(IDEMPOTENCY_KEY_UUID));
    verify(uriProperties).accountRecipientsURI(BANK_ACCOUNT_UUID);

    verifyNoMoreInteractions(requestMapper, useCase, uriProperties);
  }
}
