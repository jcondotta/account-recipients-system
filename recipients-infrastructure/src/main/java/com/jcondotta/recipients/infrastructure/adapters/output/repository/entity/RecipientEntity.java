package com.jcondotta.recipients.infrastructure.adapters.output.repository.entity;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@DynamoDbBean
@NoArgsConstructor
public class RecipientEntity {

  private String partitionKey;
  private String sortKey;
  private UUID recipientId;
  private UUID bankAccountId;
  private String recipientName;
  private String iban;
  private Instant createdAt;
  private ZoneId createdAtZoneId;

  public RecipientEntity(
      UUID recipientId,
      UUID bankAccountId,
      String recipientName,
      String iban,
      ZonedDateTime createdAt) {
    this.partitionKey = RecipientEntityKey.partitionKey(bankAccountId);
    this.sortKey = RecipientEntityKey.sortKey(recipientId);
    this.recipientId = recipientId;
    this.bankAccountId = bankAccountId;
    this.recipientName = recipientName;
    this.iban = iban;
    this.createdAt = createdAt.toInstant();
    this.createdAtZoneId = createdAt.getZone();
  }

  @DynamoDbPartitionKey
  @DynamoDbAttribute("partitionKey")
  public String getPartitionKey() {
    return partitionKey;
  }

  public void setPartitionKey(String partitionKey) {
    this.partitionKey = partitionKey;
  }

  @DynamoDbSortKey
  @DynamoDbAttribute("sortKey")
  public String getSortKey() {
    return sortKey;
  }

  public void setSortKey(String sortKey) {
    this.sortKey = sortKey;
  }

  @DynamoDbAttribute("recipientId")
  public UUID getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(UUID recipientId) {
    this.recipientId = recipientId;
  }

  @DynamoDbAttribute("bankAccountId")
  public UUID getBankAccountId() {
    return bankAccountId;
  }

  public void setBankAccountId(UUID bankAccountId) {
    this.bankAccountId = bankAccountId;
  }

  @DynamoDbAttribute("recipientName")
  @DynamoDbSecondarySortKey(indexNames = "RecipientNameLSI")
  public String getRecipientName() {
    return recipientName;
  }

  public void setRecipientName(String recipientName) {
    this.recipientName = recipientName;
  }

  @DynamoDbAttribute("iban")
  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  @DynamoDbAttribute("createdAt")
  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  @DynamoDbAttribute("createdAtZoneId")
  public ZoneId getCreatedAtZoneId() {
    return createdAtZoneId;
  }

  public void setCreatedAtZoneId(ZoneId createdAtZoneId) {
    this.createdAtZoneId = createdAtZoneId;
  }
}
