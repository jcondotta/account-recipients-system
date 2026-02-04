package com.jcondotta.recipients.infrastructure.adapters.output.repository.delete_recipient;

import com.jcondotta.recipients.domain.entities.Recipient;
import com.jcondotta.recipients.domain.exceptions.RecipientNotFoundException;
import com.jcondotta.recipients.domain.value_objects.BankAccountId;
import com.jcondotta.recipients.domain.value_objects.Iban;
import com.jcondotta.recipients.domain.value_objects.RecipientId;
import com.jcondotta.recipients.domain.value_objects.RecipientName;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntityKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipientRepositoryImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of("Jefferson Condotta");
  private static final Iban IBAN = Iban.of("GB82WEST12345698765432");

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);
  private static final ZonedDateTime CREATED_AT = ZonedDateTime.now(CLOCK);

  @Mock
  private DynamoDbTable<RecipientEntity> dynamoDbTable;

  @InjectMocks
  private DeleteRecipientRepositoryImpl deleteRepository;

  @Captor
  private ArgumentCaptor<Consumer<DeleteItemEnhancedRequest.Builder>> deleteItemConsumerCaptor;

  @Test
  void shouldDeleteRecipientSuccessfully_whenAccountRecipientExists() {
    var recipient =
        Recipient.restore(
            RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

    deleteRepository.delete(recipient);

    verify(dynamoDbTable).deleteItem(deleteItemConsumerCaptor.capture());

    var builder = DeleteItemEnhancedRequest.builder();
    deleteItemConsumerCaptor.getValue().accept(builder);
    var request = builder.build();

    assertThat(request.key().partitionKeyValue().s())
        .hasToString(RecipientEntityKey.partitionKey(BANK_ACCOUNT_ID));

    assertThat(request.key().sortKeyValue())
        .hasValueSatisfying(
            attr ->
                assertThat(attr.s())
                    .isEqualTo(RecipientEntityKey.sortKey(RECIPIENT_ID)));

    assertThat(request.conditionExpression()).isNotNull();
    assertThat(request.conditionExpression().expression())
        .isEqualTo("attribute_exists(partitionKey) AND attribute_exists(sortKey)");

    verifyNoMoreInteractions(dynamoDbTable);
  }

  @Test
  void shouldThrowAccountRecipientNotFoundException_whenRecipientDoesNotExist() {
    var recipient =
        Recipient.restore(
            RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME_JEFFERSON, IBAN, CREATED_AT);

    doThrow(ConditionalCheckFailedException.builder().build())
        .when(dynamoDbTable)
        .deleteItem(ArgumentMatchers.<Consumer<DeleteItemEnhancedRequest.Builder>>any());

    assertThatThrownBy(() -> deleteRepository.delete(recipient))
        .isInstanceOfSatisfying(
            RecipientNotFoundException.class,
            ex ->
                assertThat(ex.args())
                    .containsExactlyInAnyOrder(
                        BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value()));

    verify(dynamoDbTable).deleteItem(ArgumentMatchers.<Consumer<DeleteItemEnhancedRequest.Builder>>any());
  }
}