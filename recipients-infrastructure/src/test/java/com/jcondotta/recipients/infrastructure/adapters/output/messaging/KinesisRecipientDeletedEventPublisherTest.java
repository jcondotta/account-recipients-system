package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.recipients.common.factory.ClockTestFactory;
import com.jcondotta.recipients.common.factory.ObjectMapperTestFactory;
import com.jcondotta.recipients.domain.events.RecipientDeletedEvent;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.infrastructure.properties.RecipientsDeletedStreamProperties;
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
class KinesisRecipientDeletedEventPublisherTest {

  private static final String RECIPIENTS_DELETED_STREAM_NAME = "recipients.deleted";

  private static final IdempotencyKey IDEMPOTENCY_KEY = IdempotencyKey.newKey();

  private static final BankAccountId BANK_ACCOUNT_ID =
      new BankAccountId(UUID.randomUUID());

  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;
  private static final ZonedDateTime OCCURRED_AT = ZonedDateTime.now(FIXED_CLOCK);

  @Mock
  private KinesisAsyncClient kinesisAsyncClient;

  @Mock
  private RecipientsDeletedStreamProperties streamProperties;

  @Mock
  private EventMetadataFactory eventMetadataFactory;

  private final RecipientDeletedMessageMapper messageMapper =
      Mappers.getMapper(RecipientDeletedMessageMapper.class);

  private final ObjectMapper objectMapper =
      ObjectMapperTestFactory.getObjectMapper();

  @Captor
  private ArgumentCaptor<PutRecordRequest> putRecordRequestCaptor;

  private RecipientDeletedEvent recipientDeletedEvent;
  private KinesisRecipientDeletedEventPublisher publisher;

  @BeforeEach
  void setUp() {
    publisher = new KinesisRecipientDeletedEventPublisher(
        kinesisAsyncClient,
        messageMapper,
        streamProperties,
        eventMetadataFactory,
        objectMapper
    );

    recipientDeletedEvent = new RecipientDeletedEvent(RECIPIENT_ID, BANK_ACCOUNT_ID, OCCURRED_AT);
  }

  @Test
  void shouldPublishRecipientDeletedEvent_whenEventIsValid() {
    when(streamProperties.streamName())
        .thenReturn(RECIPIENTS_DELETED_STREAM_NAME);

    when(eventMetadataFactory.create(IDEMPOTENCY_KEY))
        .thenReturn(EventMetadata.of(IDEMPOTENCY_KEY.value()));

    when(kinesisAsyncClient.putRecord(any(PutRecordRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    publisher.send(recipientDeletedEvent, IDEMPOTENCY_KEY);

    verify(kinesisAsyncClient).putRecord(putRecordRequestCaptor.capture());
    verify(streamProperties, times(2)).streamName();
    verify(eventMetadataFactory).create(IDEMPOTENCY_KEY);
    verifyNoMoreInteractions(kinesisAsyncClient, streamProperties, eventMetadataFactory);

    assertThat(putRecordRequestCaptor.getValue())
        .satisfies(putRecordRequest -> {
          assertThat(putRecordRequest.streamName())
              .isEqualTo(RECIPIENTS_DELETED_STREAM_NAME);

          assertThat(putRecordRequest.partitionKey())
              .isEqualTo(BANK_ACCOUNT_ID.value().toString());

          var envelopeType = objectMapper.getTypeFactory().constructParametricType(
              EventEnvelope.class,
              RecipientDeletedMessage.class
          );

          EventEnvelope<RecipientDeletedMessage> eventEnvelope = objectMapper.readValue(putRecordRequest.data().asUtf8String(), envelopeType);

          assertThat(eventEnvelope.metadata())
              .satisfies(metadata -> {
                assertThat(metadata.idempotencyKey()).isEqualTo(IDEMPOTENCY_KEY.value());
                assertThat(metadata.publishedAt()).isNotNull();
              });

          assertThat(eventEnvelope.payload())
              .satisfies(payload -> {
                assertThat(payload.recipientId()).isEqualTo(recipientDeletedEvent.recipientId().value().toString());
                assertThat(payload.bankAccountId()).isEqualTo(recipientDeletedEvent.bankAccountId().value().toString());
                assertThat(payload.occurredAt()).isEqualTo(recipientDeletedEvent.occurredAt());
              });
        });
  }

  @Test
  void shouldLogErrorAndNotThrowException_whenAsyncPublishFails() {
    when(streamProperties.streamName()).thenReturn(RECIPIENTS_DELETED_STREAM_NAME);
    when(eventMetadataFactory.create(IDEMPOTENCY_KEY)).thenReturn(EventMetadata.of(IDEMPOTENCY_KEY.value()));

    when(kinesisAsyncClient.putRecord(any(PutRecordRequest.class)))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("kinesis down")));

    publisher.send(recipientDeletedEvent, IDEMPOTENCY_KEY);

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

    publisher = new KinesisRecipientDeletedEventPublisher(
        kinesisAsyncClient,
        messageMapper,
        streamProperties,
        eventMetadataFactory,
        failingObjectMapper
    );

    publisher.send(recipientDeletedEvent, IDEMPOTENCY_KEY);

    verify(eventMetadataFactory).create(IDEMPOTENCY_KEY);
    verifyNoInteractions(streamProperties, kinesisAsyncClient);
    verifyNoMoreInteractions(eventMetadataFactory);
  }
}
