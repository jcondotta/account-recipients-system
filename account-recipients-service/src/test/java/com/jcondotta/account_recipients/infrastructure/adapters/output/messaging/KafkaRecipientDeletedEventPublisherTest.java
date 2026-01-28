package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.properties.KafkaTopicsProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaRecipientDeletedEventPublisherTest {

  private static final String DELETED_RECIPIENT_TOPIC_NAME = "recipients.deleted";
  private static final String IDEMPOTENCY_KEY_HEADER = "idempotency-key";

  private static final IdempotencyKey IDEMPOTENCY_KEY = IdempotencyKey.newKey();

  private static final BankAccountId BANK_ACCOUNT_ID = new BankAccountId(UUID.randomUUID());

  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;
  private static final ZonedDateTime OCCURRED_AT = ZonedDateTime.now(FIXED_CLOCK);

  @Mock
  private KafkaTemplate<String, RecipientDeletedMessage> kafkaTemplate;

  @Mock
  private RecipientDeletedMessageMapper messageMapper;

  @Mock
  private KafkaTopicsProperties kafkaTopicsProperties;

  @Captor
  private ArgumentCaptor<ProducerRecord<String, RecipientDeletedMessage>> producerRecordCaptor;

  @Test
  void shouldPublishRecipientDeletedEventWithIdempotencyKeyHeader_whenEventIsValid() {
    var event =
        new RecipientDeletedEvent(
            RECIPIENT_ID,
            BANK_ACCOUNT_ID,
            OCCURRED_AT
        );

    var message =
        new RecipientDeletedMessage(
            RECIPIENT_ID.value().toString(),
            BANK_ACCOUNT_ID.value().toString(),
            OCCURRED_AT
        );

    when(kafkaTopicsProperties.recipientDeleted()).thenReturn(DELETED_RECIPIENT_TOPIC_NAME);
    when(messageMapper.from(event)).thenReturn(message);

    var publisher = new KafkaRecipientDeletedEventPublisher(
            kafkaTemplate,
            messageMapper,
            kafkaTopicsProperties
        );

    publisher.send(event, IDEMPOTENCY_KEY);

    verify(kafkaTemplate).send(producerRecordCaptor.capture());
    verifyNoMoreInteractions(kafkaTemplate);

    assertThat(producerRecordCaptor.getValue())
        .satisfies(producerRecord -> Assertions.assertAll(
            () -> assertThat(producerRecord.topic()).isEqualTo(DELETED_RECIPIENT_TOPIC_NAME),
            () -> assertThat(producerRecord.key()).isEqualTo(message.bankAccountId()),
            () -> assertThat(producerRecord.value()).isEqualTo(message),

            () -> {
              var header = producerRecord.headers().lastHeader(IDEMPOTENCY_KEY_HEADER);
              assertThat(header).isNotNull();
              assertThat(new String(header.value(), StandardCharsets.UTF_8))
                  .isEqualTo(IDEMPOTENCY_KEY.value().toString());
            }
        ));
  }
}
