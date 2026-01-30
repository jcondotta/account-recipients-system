package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.factory.ClockTestFactory;
import com.jcondotta.account_recipients.common.factory.ObjectMapperTestFactory;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.properties.RecipientsCreatedStreamProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KinesisRecipientCreatedEventPublisherTest {

  private static final String RECIPIENTS_CREATED_STREAM_NAME = "recipients.created";

  private static final IdempotencyKey IDEMPOTENCY_KEY = IdempotencyKey.newKey();

  private static final BankAccountId BANK_ACCOUNT_ID = new BankAccountId(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  private static final RecipientName RECIPIENT_NAME = new RecipientName(AccountRecipientFixtures.JEFFERSON.getRecipientName());
  private static final Iban IBAN = new Iban(AccountRecipientFixtures.JEFFERSON.getRecipientIban());

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;
  private static final ZonedDateTime OCCURRED_AT = ZonedDateTime.now(FIXED_CLOCK);

  @Mock
  private KinesisAsyncClient kinesisAsyncClient;

  @Mock
  private RecipientsCreatedStreamProperties streamProperties;

  @Mock
  private EventMetadataFactory eventMetadataFactory;

  private final RecipientCreatedMessageMapper messageMapper = Mappers.getMapper(RecipientCreatedMessageMapper.class);
  private final ObjectMapper objectMapper = ObjectMapperTestFactory.getObjectMapper();

  @Captor
  private ArgumentCaptor<PutRecordRequest> putRecordRequestCaptor;

  private RecipientCreatedEvent recipientCreatedEvent;

  private KinesisRecipientCreatedEventPublisher publisher;

  @BeforeEach
  void setUp() {
    publisher = new KinesisRecipientCreatedEventPublisher(
        kinesisAsyncClient,
        messageMapper,
        streamProperties,
        eventMetadataFactory,
        objectMapper
    );

    recipientCreatedEvent = new RecipientCreatedEvent(RECIPIENT_ID, RECIPIENT_NAME, BANK_ACCOUNT_ID, IBAN, OCCURRED_AT);
  }

  @Test
  void shouldPublishRecipientCreatedEvent_whenEventIsValid() {
    when(streamProperties.streamName()).thenReturn(RECIPIENTS_CREATED_STREAM_NAME);
    when(eventMetadataFactory.create(IDEMPOTENCY_KEY)).thenReturn(EventMetadata.of(IDEMPOTENCY_KEY.value()));
    when(kinesisAsyncClient.putRecord(any(PutRecordRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    publisher.send(recipientCreatedEvent, IDEMPOTENCY_KEY);

    verify(kinesisAsyncClient).putRecord(putRecordRequestCaptor.capture());
    verify(streamProperties, times(2)).streamName();
    verify(eventMetadataFactory).create(IDEMPOTENCY_KEY);
    verifyNoMoreInteractions(kinesisAsyncClient, streamProperties, eventMetadataFactory);

    assertThat(putRecordRequestCaptor.getValue())
      .satisfies(putRecordRequest -> {
        assertThat(putRecordRequest.streamName()).isEqualTo(RECIPIENTS_CREATED_STREAM_NAME);
        assertThat(putRecordRequest.partitionKey()).isEqualTo(recipientCreatedEvent.bankAccountId().value().toString());

        var envelopeType = objectMapper.getTypeFactory().constructParametricType(
                EventEnvelope.class,
                RecipientCreatedMessage.class
            );

        EventEnvelope<RecipientCreatedMessage> eventEnvelope = objectMapper.readValue(putRecordRequest.data().asUtf8String(), envelopeType);

        assertThat(eventEnvelope.metadata())
            .satisfies(metadata -> {
              assertThat(metadata.idempotencyKey()).isEqualTo(IDEMPOTENCY_KEY.value());
              assertThat(metadata.publishedAt()).isNotNull();
            });

        assertThat(eventEnvelope.payload())
            .satisfies(payload -> {
              assertThat(payload.recipientId()).isEqualTo(recipientCreatedEvent.recipientId().value().toString());
              assertThat(payload.recipientName()).isEqualTo(recipientCreatedEvent.recipientName().value());
              assertThat(payload.bankAccountId()).isEqualTo(recipientCreatedEvent.bankAccountId().value().toString());
              assertThat(payload.iban()).isEqualTo(recipientCreatedEvent.iban().value());
              assertThat(payload.occurredAt()).isEqualTo(recipientCreatedEvent.occurredAt());
            });
      });
  }

  @Test
  void shouldLogErrorAndNotThrowException_whenAsyncPublishFails() {
    when(streamProperties.streamName()).thenReturn(RECIPIENTS_CREATED_STREAM_NAME);

    when(eventMetadataFactory.create(IDEMPOTENCY_KEY)).thenReturn(EventMetadata.of(IDEMPOTENCY_KEY.value()));

    when(kinesisAsyncClient.putRecord(any(PutRecordRequest.class)))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("kinesis down")));

    publisher.send(recipientCreatedEvent, IDEMPOTENCY_KEY);

    verify(kinesisAsyncClient).putRecord(any(PutRecordRequest.class));
    verify(streamProperties, times(3)).streamName();
    verify(eventMetadataFactory).create(IDEMPOTENCY_KEY);
    verifyNoMoreInteractions(kinesisAsyncClient, streamProperties, eventMetadataFactory);
  }

  @Test
  void shouldNotPublishEvent_whenSerializationFails() throws Exception {
    when(eventMetadataFactory.create(IDEMPOTENCY_KEY))
        .thenReturn(EventMetadata.of(IDEMPOTENCY_KEY.value()));

    ObjectMapper failingObjectMapper = mock(ObjectMapper.class);

    when(failingObjectMapper.writeValueAsString(any()))
        .thenThrow(new RuntimeException("serialization failed"));

    publisher = new KinesisRecipientCreatedEventPublisher(
        kinesisAsyncClient,
        messageMapper,
        streamProperties,
        eventMetadataFactory,
        failingObjectMapper
    );

    publisher.send(recipientCreatedEvent, IDEMPOTENCY_KEY);

    verify(eventMetadataFactory).create(IDEMPOTENCY_KEY);
    verifyNoInteractions(streamProperties, kinesisAsyncClient);
    verifyNoMoreInteractions(eventMetadataFactory);
  }
}
