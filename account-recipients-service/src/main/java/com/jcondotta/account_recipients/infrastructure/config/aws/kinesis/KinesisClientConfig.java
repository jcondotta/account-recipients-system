package com.jcondotta.account_recipients.infrastructure.config.aws.kinesis;

import com.jcondotta.account_recipients.infrastructure.config.aws.EndpointOverride;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

import java.net.URI;

@Slf4j
@Configuration
public class KinesisClientConfig {

  @Bean
  public KinesisAsyncClient kinesisAsyncClient(
      Region region,
      ObjectProvider<AwsCredentialsProvider> credentialsProvider,
      ObjectProvider<EndpointOverride> endpointOverride
  ) {
    var builder = KinesisAsyncClient.builder()
        .region(region);

    credentialsProvider.ifAvailable(builder::credentialsProvider);

    endpointOverride.ifAvailable(e -> {
      log.info("Initializing KinesisAsyncClient with custom endpoint: {}", e.uri());
      builder.endpointOverride(e.uri());
    });

    return builder.build();
  }

  @Bean
  @ConditionalOnProperty(name = "cloud.aws.kinesis.endpoint")
  EndpointOverride kinesisEndpoint(@Value("${cloud.aws.kinesis.endpoint}") String endpoint) {
    return new EndpointOverride(URI.create(endpoint));
  }
}