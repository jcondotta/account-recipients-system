package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.account_recipients.application.ports.output.messaging.RecipientDeletedEventPublisher;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.infrastructure.properties.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRecipientDeletedEventPublisher implements RecipientDeletedEventPublisher {

  private static final String IDEMPOTENCY_KEY_HEADER = "idempotency-key";

  private final KafkaTemplate<String, RecipientDeletedMessage> kafkaTemplate;
  private final RecipientDeletedMessageMapper messageMapper;
  private final KafkaTopicsProperties kafkaTopicsProperties;

  @Override
  public void send(RecipientDeletedEvent event, IdempotencyKey idempotencyKey) {
    log.info(
        "Publishing RecipientDeletedEvent to Kafka [topic={}, key={}, recipientId={}, bankAccountId={}]",
        kafkaTopicsProperties.recipientDeleted(),
        event.bankAccountId(),
        event.recipientId(),
        event.bankAccountId());

    var message = messageMapper.from(event);
    var producerRecord = new ProducerRecord<>(kafkaTopicsProperties.recipientDeleted(), message.bankAccountId(), message);

    producerRecord.headers()
        .add(IDEMPOTENCY_KEY_HEADER, idempotencyKey.value().toString().getBytes(StandardCharsets.UTF_8));

    kafkaTemplate.send(producerRecord);
  }
}
