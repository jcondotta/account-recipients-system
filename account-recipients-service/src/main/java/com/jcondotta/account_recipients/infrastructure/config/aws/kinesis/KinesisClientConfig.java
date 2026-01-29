package com.jcondotta.account_recipients.infrastructure.config.aws.kinesis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

import java.net.URI;

@Slf4j
@Configuration
public class KinesisClientConfig {

  @Bean
  @ConditionalOnProperty(name = "cloud.aws.kinesis.endpoint")
  public KinesisAsyncClient kinesisAsyncClientLocal(
      AwsCredentialsProvider credentialsProvider,
      Region region,
      @Value("${cloud.aws.kinesis.endpoint}") String endpoint) {

    log.info("Initializing KinesisAsyncClient with custom endpoint: {}", endpoint);

    return KinesisAsyncClient.builder()
        .region(region)
        .endpointOverride(URI.create(endpoint))
        .credentialsProvider(credentialsProvider)
        .build();
  }
}
