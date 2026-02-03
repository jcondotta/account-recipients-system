package com.jcondotta.recipients.infrastructure.config;

import com.jcondotta.recipients.infrastructure.properties.RecipientsCreatedStreamProperties;
import com.jcondotta.recipients.infrastructure.properties.RecipientsDeletedStreamProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.aws.inbound.kinesis.CheckpointMode;
import org.springframework.integration.aws.inbound.kinesis.KinesisMessageDrivenChannelAdapter;
import org.springframework.integration.aws.inbound.kinesis.KinesisShardOffset;
import org.springframework.integration.aws.inbound.kinesis.ListenerMode;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

import java.nio.charset.StandardCharsets;

@Configuration
@Profile("test")
@EnableIntegration
public class KinesisAdapterConfig {

  @Bean
  @Qualifier("recipientsCreatedChannel")
  MessageChannel recipientsCreatedChannel() {
    return new DirectChannel();
  }

  @Bean
  @Qualifier("recipientsCreatedAdapter")
  KinesisMessageDrivenChannelAdapter recipientsCreatedAdapter(
      KinesisAsyncClient kinesisClient,
      RecipientsCreatedStreamProperties streamProperties,
      @Qualifier("recipientsCreatedChannel") MessageChannel recipientsCreatedChannel) {

    var adapter = new KinesisMessageDrivenChannelAdapter(kinesisClient, streamProperties.streamName());

    adapter.setListenerMode(ListenerMode.record);
    adapter.setConsumerGroup("recipients-created-consumer");
    adapter.setOutputChannel(recipientsCreatedChannel);
    adapter.setCheckpointMode(CheckpointMode.record);
    adapter.setConverter(converter -> new String(converter, StandardCharsets.UTF_8));


    adapter.setStreamInitialSequence(KinesisShardOffset.trimHorizon());

    return adapter;
  }

  @Bean
  @Qualifier("recipientsDeletedChannel")
  MessageChannel recipientsDeletedChannel() {
    return new DirectChannel();
  }

  @Bean
  @Qualifier("recipientsDeletedAdapter")
  KinesisMessageDrivenChannelAdapter recipientsDeletedAdapter(
      KinesisAsyncClient kinesisClient,
      RecipientsDeletedStreamProperties streamProperties,
      @Qualifier("recipientsDeletedChannel") MessageChannel recipientsCreatedChannel) {

    var adapter = new KinesisMessageDrivenChannelAdapter(kinesisClient, streamProperties.streamName());

    adapter.setListenerMode(ListenerMode.record);
    adapter.setConsumerGroup("recipients-deleted-consumer");
    adapter.setOutputChannel(recipientsCreatedChannel);
    adapter.setCheckpointMode(CheckpointMode.record);
    adapter.setConverter(converter -> new String(converter, StandardCharsets.UTF_8));


    adapter.setStreamInitialSequence(KinesisShardOffset.trimHorizon());

    return adapter;
  }
}
