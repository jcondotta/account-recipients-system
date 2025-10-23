package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients;

import com.jcondotta.account_recipients.application.ports.output.repository.shared.model.PaginatedResult;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.PaginationCursorCodec;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.GetRecipientsLastEvaluatedKey;

import java.util.List;

public class Teste {

    private PaginatedResult<AccountRecipient> paginate(List<AccountRecipient> items, int limit) {
        if (items.size() <= limit) {
            return new PaginatedResult<>(items, null);
        }

        var truncated = items.subList(0, limit);
        var last = items.get(limit - 1);

        var lek = new GetRecipientsLastEvaluatedKey(
            last.bankAccountId().value(),
            last.accountRecipientId().value(),
            last.recipientName().value()
        );
        return new PaginatedResult<>(truncated, PaginationCursorCodec.encode(lek));
    }
}
