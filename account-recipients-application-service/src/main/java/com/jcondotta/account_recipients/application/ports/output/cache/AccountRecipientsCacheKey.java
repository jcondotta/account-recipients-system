package com.jcondotta.account_recipients.application.ports.output.cache;

import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;

public interface AccountRecipientsCacheKey {

    String PREFIX_TEMPLATE = "accountRecipients::%s";

    BankAccountId bankAccountId();
    String value();

    default String rootPrefix(){
        return String.format(PREFIX_TEMPLATE, bankAccountId());
    }
}