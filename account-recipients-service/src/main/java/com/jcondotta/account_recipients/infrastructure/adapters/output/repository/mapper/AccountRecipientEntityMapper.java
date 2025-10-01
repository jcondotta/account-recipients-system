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
import org.mapstruct.factory.Mappers;

import java.util.Objects;

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

    AccountRecipientEntityMapper INSTANCE = Mappers.getMapper(AccountRecipientEntityMapper.class);

    default AccountRecipientEntity toEntity(AccountRecipient accountRecipient) {
        if(Objects.isNull(accountRecipient)){
            return null;
        }

        return new AccountRecipientEntity(
            accountRecipient.accountRecipientId().value(),
            accountRecipient.bankAccountId().value(),
            accountRecipient.recipientName().value(),
            accountRecipient.iban().value(),
            accountRecipient.createdAt()
        );
    }

    @Mapping(target = "accountRecipientId", expression = "java(AccountRecipientId.of(entity.getAccountRecipientId()))")
    @Mapping(target = "bankAccountId", expression = "java(BankAccountId.of(entity.getBankAccountId()))")
    @Mapping(target = "recipientName", expression = "java(RecipientName.of(entity.getRecipientName()))")
    @Mapping(target = "iban", expression = "java(Iban.of(entity.getIban()))")
    @Mapping(target = "createdAt", expression = "java(ZonedDateTime.ofInstant(entity.getCreatedAt(), entity.getCreatedAtZoneId()))")
    AccountRecipient toDomain(AccountRecipientEntity entity);

}
