package com.jcondotta.account_recipients.common.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.*;

public class KafkaTestConsumer<T> {

  private final Consumer<String, T> consumer;

  public KafkaTestConsumer(String bootstrapServers, String topic, Class<T> valueType) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + UUID.randomUUID());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType);

    this.consumer = new KafkaConsumer<>(props);
    this.consumer.subscribe(List.of(topic));
//    consumer.poll(Duration.ofMillis(0));
    consumer.seekToEnd(consumer.assignment());
  }

  public Optional<org.apache.kafka.clients.consumer.ConsumerRecord<String, T>> pollSingle(Duration timeout) {
    var records = consumer.poll(timeout);
    return records.isEmpty()
        ? Optional.empty()
        : Optional.of(records.iterator().next());
  }

  public void close() {
    consumer.close();
  }
}
