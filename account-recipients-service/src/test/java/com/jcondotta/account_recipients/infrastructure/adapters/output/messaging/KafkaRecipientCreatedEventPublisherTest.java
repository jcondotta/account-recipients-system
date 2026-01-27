package com.jcondotta.account_recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.account_recipients.ClockTestFactory;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
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
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaRecipientCreatedEventPublisherTest {

  private static final String CREATED_RECIPIENT_TOPIC_NAME = "recipients.created";
  private static final String IDEMPOTENCY_KEY_HEADER = "idempotency-key";

  private static final IdempotencyKey IDEMPOTENCY_KEY = IdempotencyKey.newKey();

  private static final BankAccountId BANK_ACCOUNT_ID = new BankAccountId(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  private static final RecipientName RECIPIENT_NAME = new RecipientName(AccountRecipientFixtures.JEFFERSON.getRecipientName());
  private static final Iban IBAN = new Iban(AccountRecipientFixtures.JEFFERSON.getRecipientIban());

  private static final Clock FIXED_CLOCK = ClockTestFactory.TEST_CLOCK_FIXED;
  private static final Instant OCCURRED_AT = Instant.now(FIXED_CLOCK);
  private static final ZoneId OCCURRED_AT_ZONE = FIXED_CLOCK.getZone();

  @Mock
  private KafkaTemplate<String, RecipientCreatedMessage> kafkaTemplate;

  @Mock
  private RecipientCreatedMessageMapper messageMapper;

  @Mock
  private KafkaTopicsProperties kafkaTopicsProperties;

  @Captor
  private ArgumentCaptor<ProducerRecord<String, RecipientCreatedMessage>> producerRecordCaptor;

  @Test
  void shouldPublishRecipientCreatedEventWithIdempotencyKeyHeader_whenEventIsValid() {
    var event = new RecipientCreatedEvent(
            RECIPIENT_ID,
            RECIPIENT_NAME,
            BANK_ACCOUNT_ID,
            IBAN,
            OCCURRED_AT,
            OCCURRED_AT_ZONE
        );

    var message =
        new RecipientCreatedMessage(
            RECIPIENT_ID.value().toString(),
            RECIPIENT_NAME.value(),
            BANK_ACCOUNT_ID.value().toString(),
            IBAN.value(),
            OCCURRED_AT,
            OCCURRED_AT_ZONE.getId()
        );

    when(kafkaTopicsProperties.recipientCreated()).thenReturn(CREATED_RECIPIENT_TOPIC_NAME);
    when(messageMapper.from(event)).thenReturn(message);

    var publisher =
        new KafkaRecipientCreatedEventPublisher(
            kafkaTemplate,
            messageMapper,
            kafkaTopicsProperties
        );

    publisher.send(event, IDEMPOTENCY_KEY);

    verify(kafkaTemplate).send(producerRecordCaptor.capture());
    verifyNoMoreInteractions(kafkaTemplate);

    assertThat(producerRecordCaptor.getValue())
        .satisfies(producerRecord -> Assertions.assertAll(
            () -> assertThat(producerRecord.topic()).isEqualTo(CREATED_RECIPIENT_TOPIC_NAME),
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
