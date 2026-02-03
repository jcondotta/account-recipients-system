package com.jcondotta.recipients.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.recipients.common.factory.ObjectMapperTestFactory;
import com.jcondotta.recipients.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.recipients.infrastructure.adapters.output.messaging.RecipientCreatedMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@Profile("test")
public class RecipientsCreatedTestListener2 {

  private final BlockingQueue<EventEnvelope<RecipientCreatedMessage>> events = new LinkedBlockingQueue<>();
  private final ObjectMapper objectMapper = ObjectMapperTestFactory.getObjectMapper();

  @ServiceActivator(inputChannel = "recipientsCreatedChannel")
  public void onMessage(Message<String> message) {
    try {
      EventEnvelope<RecipientCreatedMessage> envelope =
          objectMapper.readValue(message.getPayload(),
              objectMapper.getTypeFactory().constructParametricType(
                  EventEnvelope.class,
                  RecipientCreatedMessage.class
              )
          );

      events.add(envelope);

    } catch (Exception e) {
      throw new IllegalStateException("Failed to deserialize RecipientCreatedEvent", e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> EventEnvelope<T> awaitEvent(
      Duration timeout,
      Class<T> payloadType,
      java.util.function.Predicate<EventEnvelope<T>> filter
  ) throws InterruptedException {

    long deadline = System.currentTimeMillis() + timeout.toMillis();

    while (System.currentTimeMillis() < deadline) {
      EventEnvelope<?> raw = events.poll(100, TimeUnit.MILLISECONDS);

      if (raw == null) continue;

      Object payload = raw.payload();

      if (!payloadType.isInstance(payload)) {
        continue;
      }

      EventEnvelope<T> typed = new EventEnvelope<>(raw.metadata(), payloadType.cast(payload));

      if (filter.test(typed)) {
        return typed;
      }
    }

    return null;
  }

  public void clear() {
    events.clear();
  }
}