package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.model.PaginatedResult;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.PaginationCursor;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.QueryLimit;
import com.jcondotta.account_recipients.application.ports.output.repository.shared.value_objects.RecipientNamePrefix;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.PaginationCursorCodec;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.mapper.GetRecipientsLastEvaluatedKeyMapper;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.GetRecipientsLastEvaluatedKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.internal.conditional.BeginsWithConditional;
import software.amazon.awssdk.enhanced.dynamodb.internal.conditional.EqualToConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAccountRecipientsRepositoryImplTest {

  @Mock
  private DynamoDbIndex<AccountRecipientEntity> dynamoDbIndex;

  @Mock
  private AccountRecipientEntityMapper entityMapper;

  @Mock
  private GetRecipientsLastEvaluatedKeyMapper lastEvaluatedKeyMapper;

  private MeterRegistry meterRegistry;

  private GetAccountRecipientsRepositoryImpl repository;

  private BankAccountId bankAccountId;

  @BeforeEach
  void setup() {
    meterRegistry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();

    repository =
        new GetAccountRecipientsRepositoryImpl(
            dynamoDbIndex,
            entityMapper,
            lastEvaluatedKeyMapper,
            meterRegistry);

    bankAccountId = BankAccountId.of(UUID.randomUUID());
  }

  // ----------------------------------------------------------------------
  // buildQueryConditional
  // ----------------------------------------------------------------------

  @Nested
  class BuildQueryConditional {

    @Test
    void shouldBuildKeyEqualTo_whenNamePrefixIsNull() {
      var params = GetAccountRecipientsQueryParams.of(QueryLimit.of(5));
      var query = GetAccountRecipientsQuery.of(bankAccountId, params);

      var conditional = repository.buildQueryConditional(query);

      assertThat(conditional)
          .isInstanceOf(EqualToConditional.class);
    }

    @Test
    void shouldBuildSortBeginsWith_whenNamePrefixIsPresent() {
      var params =
          GetAccountRecipientsQueryParams.of(
              QueryLimit.of(5),
              RecipientNamePrefix.of("Je"),
              null);

      var query = GetAccountRecipientsQuery.of(bankAccountId, params);

      QueryConditional conditional = repository.buildQueryConditional(query);

      assertThat(conditional)
          .isInstanceOf(BeginsWithConditional.class);
    }
  }

  // ----------------------------------------------------------------------
  // findByQuery
  // ----------------------------------------------------------------------

  @Test
  void shouldReturnEmpty_whenNoPagesReturned() {
    var params = GetAccountRecipientsQueryParams.of(QueryLimit.of(5));
    var query = GetAccountRecipientsQuery.of(bankAccountId, params);

    when(dynamoDbIndex.query(any(QueryEnhancedRequest.class)))
        .thenReturn(Collections::emptyIterator);

    PaginatedResult<AccountRecipient> result = repository.findByQuery(query);

    assertThat(result.items()).isEmpty();
    assertThat(result.nextCursor()).isNull();
  }

  @Test
  void shouldReturnEmptyAndRecordEmptyResult_whenPageHasNoItems() {
    var params = GetAccountRecipientsQueryParams.of(QueryLimit.of(5));
    var query = GetAccountRecipientsQuery.of(bankAccountId, params);

    Page<AccountRecipientEntity> page = mock(Page.class);
    when(page.items()).thenReturn(List.of());

    when(dynamoDbIndex.query(any(QueryEnhancedRequest.class)))
        .thenReturn(() -> List.of(page).iterator());

    PaginatedResult<AccountRecipient> result = repository.findByQuery(query);

    assertThat(result.items()).isEmpty();
    assertThat(result.nextCursor()).isNull();
  }

  @Test
  void shouldReturnItemsWithoutCursor_whenWithinLimit() {
    var params = GetAccountRecipientsQueryParams.of(QueryLimit.of(2));
    var query = GetAccountRecipientsQuery.of(bankAccountId, params);

    AccountRecipientEntity entity = mock(AccountRecipientEntity.class);
    AccountRecipient domain = mock(AccountRecipient.class);

    Page<AccountRecipientEntity> page = mock(Page.class);
    when(page.items()).thenReturn(List.of(entity));
    when(entityMapper.toDomain(entity)).thenReturn(domain);

    when(dynamoDbIndex.query(any(QueryEnhancedRequest.class)))
        .thenReturn(() -> List.of(page).iterator());

    PaginatedResult<AccountRecipient> result = repository.findByQuery(query);

    assertThat(result.items()).containsExactly(domain);
    assertThat(result.nextCursor()).isNull();
  }

  @Test
  void shouldReturnItemsWithCursor_whenExceedsLimit() {
    var params = GetAccountRecipientsQueryParams.of(QueryLimit.of(1));
    var query = GetAccountRecipientsQuery.of(bankAccountId, params);

    AccountRecipientEntity e1 = mock(AccountRecipientEntity.class);
    AccountRecipientEntity e2 = mock(AccountRecipientEntity.class);

    AccountRecipient r1 = mock(AccountRecipient.class);
    AccountRecipient r2 = mock(AccountRecipient.class);

    when(r1.getBankAccountId()).thenReturn(bankAccountId);
    when(r1.getRecipientId()).thenReturn(RecipientId.newId());
    when(r1.getRecipientName()).thenReturn(RecipientName.of("A"));

    Page<AccountRecipientEntity> page = mock(Page.class);
    when(page.items()).thenReturn(List.of(e1, e2));

    when(entityMapper.toDomain(e1)).thenReturn(r1);
    when(entityMapper.toDomain(e2)).thenReturn(r2);

    when(dynamoDbIndex.query(any(QueryEnhancedRequest.class)))
        .thenReturn(() -> List.of(page).iterator());

    PaginatedResult<AccountRecipient> result = repository.findByQuery(query);

    assertThat(result.items()).hasSize(1);
    assertThat(result.nextCursor()).isNotNull();
  }

  @Test
  void shouldIgnoreCursor_whenStartKeyDoesNotBelongToSamePartition() {
    var otherBankAccountId = BankAccountId.of(UUID.randomUUID());

    var lek =
        new GetRecipientsLastEvaluatedKey(
            otherBankAccountId.value(),
            UUID.randomUUID(),
            "Someone");

    var encodedCursor = PaginationCursorCodec.encode(lek);

    var params =
        GetAccountRecipientsQueryParams.of(
            QueryLimit.of(5),
            null,
            PaginationCursor.of(encodedCursor));

    var query = GetAccountRecipientsQuery.of(bankAccountId, params);

    // start key decodificado, mas com PK errada
    when(lastEvaluatedKeyMapper.toMap(any()))
        .thenReturn(
            Map.of(
                GetRecipientsLastEvaluatedKeyMapper.PARTITION_KEY_PARAM_NAME,
                software.amazon.awssdk.services.dynamodb.model.AttributeValue
                    .builder()
                    .s(AccountRecipientEntityKey.partitionKey(otherBankAccountId))
                    .build()));

    Page<AccountRecipientEntity> page = mock(Page.class);
    when(page.items()).thenReturn(List.of());

    when(dynamoDbIndex.query(any(QueryEnhancedRequest.class)))
        .thenReturn(() -> List.of(page).iterator());

    PaginatedResult<AccountRecipient> result = repository.findByQuery(query);

    assertThat(result.items()).isEmpty();
    assertThat(result.nextCursor()).isNull();
  }


  @Test
  void shouldCatchDynamoDbException_andReturnEmpty() {
    var params = GetAccountRecipientsQueryParams.of(QueryLimit.of(5));
    var query = GetAccountRecipientsQuery.of(bankAccountId, params);

    when(dynamoDbIndex.query(any(QueryEnhancedRequest.class)))
        .thenThrow(DynamoDbException.builder().message("boom").build());

    PaginatedResult<AccountRecipient> result = repository.findByQuery(query);

    assertThat(result.items()).isEmpty();
    assertThat(result.nextCursor()).isNull();
  }
}
