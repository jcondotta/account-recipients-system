package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper;

import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntityKey;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
    builder = @Builder(disableBuilder = true),
    imports = {
        AccountRecipientEntityKey.class,
        AccountRecipientId.class,
        BankAccountId.class,
        RecipientName.class,
        Iban.class,
    })
public interface AccountRecipientEntityMapper {

    @Mapping(target = "partitionKey", expression = "java(AccountRecipientEntityKey.partitionKey(accountRecipient.bankAccountId()))")
    @Mapping(target = "sortKey", expression = "java(AccountRecipientEntityKey.sortKey(accountRecipient.accountRecipientId()))")
    @Mapping(target = "accountRecipientId", expression = "java(accountRecipient.accountRecipientId().value())")
    @Mapping(target = "bankAccountId", expression = "java(accountRecipient.bankAccountId().value())")
    @Mapping(target = "recipientName", expression = "java(accountRecipient.recipientName().value())")
    @Mapping(target = "iban", expression = "java(accountRecipient.iban().value())")
    @Mapping(target = "createdAt", expression = "java(accountRecipient.createdAt().toInstant())")
    @Mapping(target = "createdAtZoneId", expression = "java(accountRecipient.createdAt().getZone())")
    AccountRecipientEntity toEntity(AccountRecipient accountRecipient);

    @Mapping(target = "accountRecipientId", expression = "java(AccountRecipientId.of(entity.getAccountRecipientId()))")
    @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(entity.getBankAccountId()))")
    @Mapping(target = "recipientName", expression = "java(RecipientName.of(entity.getRecipientName()))")
    @Mapping(target = "iban", expression = "java(Iban.of(entity.getIban()))")
    @Mapping(target = "createdAt", expression = "java(ZonedDateTime.ofInstant(entity.getCreatedAt(), entity.getCreatedAtZoneId()))")
    AccountRecipient toDomain(AccountRecipientEntity entity);

}
