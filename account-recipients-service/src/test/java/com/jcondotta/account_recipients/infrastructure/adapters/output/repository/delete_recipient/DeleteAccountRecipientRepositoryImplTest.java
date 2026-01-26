package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.delete_recipient;

import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DeleteAccountRecipientRepositoryImplTest {

  private static final RecipientId ACCOUNT_RECIPIENT_ID = RecipientId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Mock
  private DynamoDbTable<AccountRecipientEntity> dynamoDbTable;

  @InjectMocks
  private DeleteAccountRecipientRepositoryImpl deleteRepository;

  @Captor
  private ArgumentCaptor<DeleteItemEnhancedRequest> requestCaptor;

//  @Test
//  void shouldDeleteRecipientSuccessfully_whenAccountRecipientExists() {
//    deleteRepository.delete(BANK_ACCOUNT_ID, ACCOUNT_RECIPIENT_ID);
//
//    verify(dynamoDbTable).deleteItem(requestCaptor.capture());
//    assertThat(requestCaptor.getValue())
//        .satisfies(
//            request -> {
//              assertThat(request.key().partitionKeyValue().s())
//                  .hasToString(AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID));
//              assertThat(request.key().sortKeyValue())
//                  .hasValueSatisfying(
//                      attr ->
//                          assertThat(attr.s())
//                              .isEqualTo(AccountRecipientEntityKey.sortKey(ACCOUNT_RECIPIENT_ID)));
//              assertThat(request.conditionExpression()).isNotNull();
//              assertThat(request.conditionExpression().expression())
//                  .isEqualTo("attribute_exists(partitionKey) AND attribute_exists(sortKey)");
//            });
//
//    verifyNoMoreInteractions(dynamoDbTable);
//  }
//
//  @Test
//  void shouldThrowAccountRecipientNotFoundException_whenRecipientDoesNotExist() {
//    doThrow(ConditionalCheckFailedException.builder().build())
//        .when(dynamoDbTable)
//        .deleteItem(any(DeleteItemEnhancedRequest.class));
//
//    assertThatThrownBy(() -> deleteRepository.delete(BANK_ACCOUNT_ID, ACCOUNT_RECIPIENT_ID))
//        .isInstanceOfSatisfying(
//            AccountRecipientNotFoundException.class,
//            ex ->
//                assertThat(ex.getIdentifiers())
//                    .containsExactlyInAnyOrder(
//                        BANK_ACCOUNT_ID.value(), ACCOUNT_RECIPIENT_ID.value()));
//
//    verify(dynamoDbTable).deleteItem(any(DeleteItemEnhancedRequest.class));
//  }
}
