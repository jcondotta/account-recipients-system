package com.jcondotta.account_recipients.infrastructure.config;

import com.jcondotta.account_recipients.infrastructure.properties.RecipientsCreatedStreamProperties;
import com.jcondotta.account_recipients.infrastructure.properties.RecipientsDeletedStreamProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aws.inbound.kinesis.KinesisMessageDrivenChannelAdapter;
import org.springframework.integration.aws.inbound.kinesis.ListenerMode;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

@Configuration
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
      @Qualifier("recipientsDeletedChannel") MessageChannel recipientsDeletedChannel) {
    var adapter = new KinesisMessageDrivenChannelAdapter(kinesisClient, streamProperties.streamName());

    adapter.setListenerMode(ListenerMode.record);
    adapter.setConsumerGroup("recipients-deleted-consumer");
    adapter.setOutputChannel(recipientsDeletedChannel);

    return adapter;
  }
}
