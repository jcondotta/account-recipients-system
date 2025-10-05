package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.mapper;

import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.GetRecipientsLastEvaluatedKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full coverage unit tests for {@link GetRecipientsLastEvaluatedKeyMapper}.
 */
class GetRecipientsLastEvaluatedKeyMapperTest {

    private GetRecipientsLastEvaluatedKeyMapper mapper;

    private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID ACCOUNT_RECIPIENT_ID = UUID.randomUUID();
    private static final String RECIPIENT_NAME = "Jefferson Condotta";

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(GetRecipientsLastEvaluatedKeyMapper.class);
    }

    // --- toMap() ---

    @Test
    void shouldReturnNull_whenKeyIsNull() {
        assertThat(mapper.toMap(null)).isNull();
    }

    @Test
    void shouldMapToAttributeValueMap_whenKeyIsValid() {
        // given
        var key = GetRecipientsLastEvaluatedKey.of(BANK_ACCOUNT_ID, ACCOUNT_RECIPIENT_ID, RECIPIENT_NAME);

        // when
        Map<String, AttributeValue> map = mapper.toMap(key);

        // then
        assertThat(map)
            .hasSize(3)
            .containsKeys(
                GetRecipientsLastEvaluatedKeyMapper.PARTITION_KEY_PARAM_NAME,
                GetRecipientsLastEvaluatedKeyMapper.SORT_KEY_PARAM_NAME,
                GetRecipientsLastEvaluatedKeyMapper.RECIPIENT_NAME_LSI_PARAM_NAME
            )
            .satisfies(it -> {
                var pk = it.get(GetRecipientsLastEvaluatedKeyMapper.PARTITION_KEY_PARAM_NAME).s();
                var sk = it.get(GetRecipientsLastEvaluatedKeyMapper.SORT_KEY_PARAM_NAME).s();
                var rn = it.get(GetRecipientsLastEvaluatedKeyMapper.RECIPIENT_NAME_LSI_PARAM_NAME).s();

                assertThat(pk).contains(BANK_ACCOUNT_ID.toString());
                assertThat(sk).contains(ACCOUNT_RECIPIENT_ID.toString());
                assertThat(rn).isEqualTo(RECIPIENT_NAME);
            });
    }

    // --- extractBankAccountId() ---

    @Test
    void shouldReturnNull_whenMapIsNullInExtractBankAccountId() {
        assertThat(GetRecipientsLastEvaluatedKeyMapper.extractBankAccountId(null)).isNull();
    }

    @Test
    void shouldReturnNull_whenPartitionKeyIsMissing() {
        var map = Map.of("otherKey", AttributeValue.fromS("something"));
        assertThat(GetRecipientsLastEvaluatedKeyMapper.extractBankAccountId(map)).isNull();
    }

    @Test
    void shouldExtractBankAccountId_whenPartitionKeyExists() {
        var pk = AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID);
        var map = Map.of(GetRecipientsLastEvaluatedKeyMapper.PARTITION_KEY_PARAM_NAME, AttributeValue.fromS(pk));

        UUID result = GetRecipientsLastEvaluatedKeyMapper.extractBankAccountId(map);

        assertThat(result).isEqualTo(BANK_ACCOUNT_ID);
    }

    // --- extractAccountRecipientId() ---

    @Test
    void shouldReturnNull_whenMapIsNullInExtractAccountRecipientId() {
        assertThat(GetRecipientsLastEvaluatedKeyMapper.extractAccountRecipientId(null)).isNull();
    }

    @Test
    void shouldReturnNull_whenSortKeyIsMissing() {
        var map = Map.of("anotherKey", AttributeValue.fromS("value"));
        assertThat(GetRecipientsLastEvaluatedKeyMapper.extractAccountRecipientId(map)).isNull();
    }

    @Test
    void shouldExtractAccountRecipientId_whenSortKeyExists() {
        var sk = AccountRecipientEntityKey.sortKey(ACCOUNT_RECIPIENT_ID);
        var map = Map.of(GetRecipientsLastEvaluatedKeyMapper.SORT_KEY_PARAM_NAME, AttributeValue.fromS(sk));

        UUID result = GetRecipientsLastEvaluatedKeyMapper.extractAccountRecipientId(map);

        assertThat(result).isEqualTo(ACCOUNT_RECIPIENT_ID);
    }

    // --- extractRecipientName() ---

    @Test
    void shouldReturnNull_whenMapIsNullInExtractRecipientName() {
        assertThat(GetRecipientsLastEvaluatedKeyMapper.extractRecipientName(null)).isNull();
    }

    @Test
    void shouldReturnNull_whenRecipientNameIsMissing() {
        var map = Map.of("someKey", AttributeValue.fromS("irrelevant"));
        assertThat(GetRecipientsLastEvaluatedKeyMapper.extractRecipientName(map)).isNull();
    }

    @Test
    void shouldExtractRecipientName_whenKeyExists() {
        var map = Map.of(GetRecipientsLastEvaluatedKeyMapper.RECIPIENT_NAME_LSI_PARAM_NAME,
            AttributeValue.fromS(RECIPIENT_NAME));

        String result = GetRecipientsLastEvaluatedKeyMapper.extractRecipientName(map);

        assertThat(result).isEqualTo(RECIPIENT_NAME);
    }

    // --- toDomain() (composed mapping) ---

    @Test
    void shouldMapToDomain_whenMapContainsAllAttributes() {
        // given
        var pk = AccountRecipientEntityKey.partitionKey(BANK_ACCOUNT_ID);
        var sk = AccountRecipientEntityKey.sortKey(ACCOUNT_RECIPIENT_ID);

        Map<String, AttributeValue> map = new HashMap<>();
        map.put(GetRecipientsLastEvaluatedKeyMapper.PARTITION_KEY_PARAM_NAME, AttributeValue.fromS(pk));
        map.put(GetRecipientsLastEvaluatedKeyMapper.SORT_KEY_PARAM_NAME, AttributeValue.fromS(sk));
        map.put(GetRecipientsLastEvaluatedKeyMapper.RECIPIENT_NAME_LSI_PARAM_NAME, AttributeValue.fromS(RECIPIENT_NAME));

        // when
        GetRecipientsLastEvaluatedKey result = mapper.toDomain(map);

        // then
        assertThat(result)
            .satisfies(it -> {
                assertThat(it.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
                assertThat(it.accountRecipientId()).isEqualTo(ACCOUNT_RECIPIENT_ID);
                assertThat(it.recipientName()).isEqualTo(RECIPIENT_NAME);
            });
    }

    @Test
    void shouldMapToDomainWithNulls_whenMapIsMissingFields() {
        Map<String, AttributeValue> map = Map.of(
            GetRecipientsLastEvaluatedKeyMapper.RECIPIENT_NAME_LSI_PARAM_NAME,
            AttributeValue.fromS(RECIPIENT_NAME)
        );

        Assertions.assertThatThrownBy(() -> mapper.toDomain(map))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("bank account id must not be null");
    }

    @Test
    void shouldReturnNull_whenMapIsNullInToDomain() {
        // when
        GetRecipientsLastEvaluatedKey result = mapper.toDomain(null);

        // then
        assertThat(result).isNull();
    }
}
