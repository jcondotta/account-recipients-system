package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientDeletedEventPublisher;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.infrastructure.properties.RecipientsDeletedStreamProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class KinesisRecipientDeletedEventPublisher
    implements RecipientDeletedEventPublisher {

  private final KinesisAsyncClient kinesisAsyncClient;
  private final RecipientDeletedMessageMapper messageMapper;
  private final RecipientsDeletedStreamProperties streamProperties;
  private final EventMetadataFactory eventMetadataFactory;
  private final ObjectMapper objectMapper;

  @Override
  public void send(RecipientDeletedEvent deletedEvent, IdempotencyKey idempotencyKey) {
    var eventMetadata = eventMetadataFactory.create(idempotencyKey);
    var eventPayload = messageMapper.fromEvent(deletedEvent);
    var eventEnvelope = EventEnvelope.of(eventMetadata, eventPayload);

    try {
      var payload = objectMapper.writeValueAsString(eventEnvelope);

      var request = PutRecordRequest.builder()
          .streamName(streamProperties.streamName())
          .partitionKey(deletedEvent.bankAccountId().value().toString())
          .data(SdkBytes.fromString(payload, StandardCharsets.UTF_8))
          .build();

      log.info(
          "Publishing RecipientDeletedEvent to Kinesis " +
              "[stream={}, partitionKey={}, recipientId={}, bankAccountId={}]",
          streamProperties.streamName(),
          deletedEvent.bankAccountId().value(),
          deletedEvent.recipientId().value(),
          deletedEvent.bankAccountId().value()
      );

      kinesisAsyncClient.putRecord(request)
          .exceptionally(ex -> {
            log.error(
                "Failed to publish RecipientDeletedEvent to Kinesis " + "[stream={}, recipientId={}]",
                streamProperties.streamName(),
                deletedEvent.recipientId().value(),
                ex
            );
            return null;
          });

    } catch (Exception ex) {
      log.error(
          "Failed to serialize RecipientDeletedEvent [recipientId={}, bankAccountId={}]",
          deletedEvent.recipientId().value(),
          deletedEvent.bankAccountId().value(),
          ex
      );
    }
  }
}
