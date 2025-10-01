package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.mapper;

import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.LastEvaluatedKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LastEvaluatedKeyMapper {

    String PARTITION_KEY_PARAM_NAME = "partitionKey";
    String SORT_KEY_PARAM_NAME = "sortKey";
    String RECIPIENT_NAME_LSI_PARAM_NAME = "recipientName";

    @Mapping(target = "bankAccountId", source = "map", qualifiedByName = "extractBankAccountId")
    @Mapping(target = "accountRecipientId", source = "map", qualifiedByName = "extractAccountRecipientId")
    @Mapping(target = "recipientName", source = "map", qualifiedByName = "extractRecipientName")
    LastEvaluatedKey toDomain(Map<String, AttributeValue> map);

    @Named("toMap")
    default Map<String, AttributeValue> toMap(LastEvaluatedKey key) {
        if (key == null) return null;
        return Map.of(
            PARTITION_KEY_PARAM_NAME, AttributeValue.fromS(key.bankAccountId().toString()),
            SORT_KEY_PARAM_NAME, AttributeValue.fromS(key.accountRecipientId().toString()),
            RECIPIENT_NAME_LSI_PARAM_NAME, AttributeValue.fromS(key.recipientName())
        );
    }

    @Named("extractBankAccountId")
    static UUID extractBankAccountId(Map<String, AttributeValue> map) {
        if (map == null || !map.containsKey(PARTITION_KEY_PARAM_NAME)) return null;
        var partitionKey = map.get(PARTITION_KEY_PARAM_NAME).s();
        return AccountRecipientEntityKey.extractBankAccountId(partitionKey).value();
    }

    @Named("extractAccountRecipientId")
    static UUID extractAccountRecipientId(Map<String, AttributeValue> map) {
        if (map == null || !map.containsKey(SORT_KEY_PARAM_NAME)) return null;
        var sortKey = map.get(SORT_KEY_PARAM_NAME).s();
        return AccountRecipientEntityKey.extractAccountRecipientId(sortKey).value();
    }

    @Named("extractRecipientName")
    static String extractRecipientName(Map<String, AttributeValue> map) {
        if (map == null || !map.containsKey(RECIPIENT_NAME_LSI_PARAM_NAME)) return null;
        return map.get(RECIPIENT_NAME_LSI_PARAM_NAME).s();
    }
}
