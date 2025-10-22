package com.jcondotta.account_recipients.create_recipient.controller;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.application.usecase.create_recipient.CreateAccountRecipientUseCase;
import com.jcondotta.account_recipients.application.usecase.create_recipient.model.CreateAccountRecipientCommand;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.create_recipient.controller.mapper.CreateAccountRecipientRequestRestMapper;
import com.jcondotta.account_recipients.create_recipient.controller.model.CreateAccountRecipientRestRequest;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientURIProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountRecipientControllerImplTest {

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
    private static final UUID IDEMPOTENCY_KEY_UUID = UUID.randomUUID();

    private static final String RECIPIENT_NAME = AccountRecipientFixtures.JEFFERSON.getRecipientName();
    private static final String IBAN = AccountRecipientFixtures.JEFFERSON.getRecipientIban();

    private static final URI EXPECTED_LOCATION_URI = URI
        .create("https://api.jcondotta.com/v1/bank-accounts/" + BANK_ACCOUNT_UUID + "/account-recipients");

    @Mock
    private CreateAccountRecipientCommand createAccountRecipientCommand;

    @Mock
    private CreateAccountRecipientUseCase useCase;

    @Mock
    private CreateAccountRecipientRequestRestMapper requestMapper;

    @Mock
    private AccountRecipientURIProperties uriProperties;

    @Captor
    private ArgumentCaptor<CreateAccountRecipientCommand> commandCaptor;

    private Clock fixedClock = ClockTestFactory.TEST_CLOCK_FIXED;
    private CreateAccountRecipientControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new CreateAccountRecipientControllerImpl(useCase, requestMapper, uriProperties, fixedClock);
    }

    @Test
    void shouldCreateAccountRecipientAndReturnCreatedResponse_whenRequestIsValid() {
        var request = CreateAccountRecipientRestRequest.of(RECIPIENT_NAME, IBAN);

        when(requestMapper.toCommand(BANK_ACCOUNT_UUID, request, fixedClock))
            .thenReturn(createAccountRecipientCommand);

        when(uriProperties.accountRecipientsURI(BANK_ACCOUNT_UUID)).thenReturn(EXPECTED_LOCATION_URI);

        ResponseEntity<String> response = controller.createAccountRecipient(IDEMPOTENCY_KEY_UUID, BANK_ACCOUNT_UUID, request);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getLocation()).isEqualTo(EXPECTED_LOCATION_URI);
        assertThat(response.getBody()).isNull();

        verify(requestMapper).toCommand(BANK_ACCOUNT_UUID, request, fixedClock);
        verify(useCase).execute(createAccountRecipientCommand, IdempotencyKey.of(IDEMPOTENCY_KEY_UUID));
        verify(uriProperties).accountRecipientsURI(BANK_ACCOUNT_UUID);

        verifyNoMoreInteractions(requestMapper, useCase, uriProperties);
    }
}
