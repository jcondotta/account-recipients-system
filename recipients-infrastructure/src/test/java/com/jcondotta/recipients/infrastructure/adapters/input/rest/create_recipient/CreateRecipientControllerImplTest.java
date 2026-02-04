package com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient;

import com.jcondotta.recipients.application.usecase.create_recipient.CreateRecipientUseCase;
import com.jcondotta.recipients.application.usecase.create_recipient.model.CreateRecipientCommand;
import com.jcondotta.recipients.common.fixtures.RecipientFixtures;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.mapper.CreateRecipientRequestRestMapper;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.recipients.infrastructure.properties.RecipientURIProperties;
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

  private static final String RECIPIENT_NAME = RecipientFixtures.JEFFERSON.getRecipientName();
  private static final String IBAN = RecipientFixtures.JEFFERSON.getRecipientIban();

  private static final URI EXPECTED_LOCATION_URI =
      URI.create("https://api.jcondotta.com/v1/bank-accounts/" + BANK_ACCOUNT_UUID + "/recipients");

  @Mock
  private CreateRecipientCommand createRecipientCommand;

  @Mock
  private CreateRecipientUseCase useCase;

  @Mock
  private CreateRecipientRequestRestMapper requestMapper;

  @Mock
  private RecipientURIProperties uriProperties;

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

    when(uriProperties.recipientsURI(BANK_ACCOUNT_UUID)).thenReturn(EXPECTED_LOCATION_URI);

    ResponseEntity<String> response =
        controller.createAccountRecipient(BANK_ACCOUNT_UUID, request);

    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getHeaders().getLocation()).isEqualTo(EXPECTED_LOCATION_URI);
    assertThat(response.getBody()).isNull();

    verify(requestMapper).toCommand(BANK_ACCOUNT_UUID, request);
    verify(useCase).execute(createRecipientCommand);
    verify(uriProperties).recipientsURI(BANK_ACCOUNT_UUID);

    verifyNoMoreInteractions(requestMapper, useCase, uriProperties);
  }
}
