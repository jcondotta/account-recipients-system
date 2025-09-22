package com.jcondotta.account_recipients.infrastructure.config.aws.dynamodb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Slf4j
@Configuration
public class DynamoDbClientConfig {

    @Bean
    @ConditionalOnProperty(name = "cloud.aws.dynamodb.endpoint")
    public DynamoDbClient dynamoDbClientLocal(AwsCredentialsProvider credentialsProvider, Region region,
                                              @Value("${cloud.aws.dynamodb.endpoint}") String endpoint) {

        log.info("Initializing DynamoDbClient with custom endpoint: {}", endpoint);

        return DynamoDbClient.builder()
            .region(region)
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Bean
    @ConditionalOnMissingBean(DynamoDbClient.class)
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider credentialsProvider, Region region) {
        return DynamoDbClient.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build();
    }
}
