package com.jcondotta.recipients.infrastructure.adapters.output.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.recipients.application.ports.output.messaging.RecipientCreatedEventPublisher;
import com.jcondotta.recipients.domain.events.RecipientCreatedEvent;
import com.jcondotta.recipients.infrastructure.properties.RecipientsCreatedStreamProperties;
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
public class KinesisRecipientCreatedEventPublisher implements RecipientCreatedEventPublisher {

  private final KinesisAsyncClient kinesisAsyncClient;
  private final RecipientCreatedMessageMapper messageMapper;
  private final RecipientsCreatedStreamProperties streamProperties;
  private final EventMetadataFactory eventMetadataFactory;
  private final ObjectMapper objectMapper;

  @Override
  public void publish(RecipientCreatedEvent createdEvent) {
    var eventMetadata = eventMetadataFactory.create();
    var eventPayload = messageMapper.fromEvent(createdEvent);
    var eventEnvelope = EventEnvelope.of(eventMetadata, eventPayload);

    try {
      var payload = objectMapper.writeValueAsString(eventEnvelope);

      var request = PutRecordRequest.builder()
          .streamName(streamProperties.streamName())
          .partitionKey(createdEvent.bankAccountId().value().toString())
          .data(SdkBytes.fromString(payload, StandardCharsets.UTF_8))
          .build();

      log.info(
          "Publishing RecipientCreatedEvent to Kinesis [stream={}, partitionKey={}, recipientId={}, bankAccountId={}]",
          streamProperties.streamName(),
          createdEvent.bankAccountId().value(),
          createdEvent.recipientId().value(),
          createdEvent.bankAccountId().value()
      );

      kinesisAsyncClient.putRecord(request)
          .exceptionally(ex -> {
            log.error(
                "Failed to publish RecipientCreatedEvent to Kinesis [stream={}, recipientId={}]",
                streamProperties.streamName(),
                createdEvent.recipientId().value(),
                ex
            );
            return null;
          }).join();

    } catch (Exception ex) {
      log.error(
          "Failed to serialize RecipientCreatedEvent [recipientId={}, bankAccountId={}]",
          createdEvent.recipientId().value(),
          createdEvent.bankAccountId().value(),
          ex
      );
    }
  }
}
