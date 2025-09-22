package com.jcondotta.account_recipients.get_recipients.by_account_id;

import com.jcondotta.account_recipients.application.ports.output.repository.GetAccountRecipientsRepository;
import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.GetAccountRecipientsUseCase;
import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.mapper.GetAccountRecipientsMapper;
import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.by_account_id.model.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAccountRecipientsUseCaseImpl implements GetAccountRecipientsUseCase {

    private final GetAccountRecipientsMapper commandMapper;
    private final GetAccountRecipientsRepository getAccountRecipientsRepository;

    @Override
    public GetAccountRecipientsResult execute(GetAccountRecipientsQuery getAccountRecipientsQuery) {
        List<AccountRecipient> accountRecipients = getAccountRecipientsRepository.byBankAccountId(getAccountRecipientsQuery.bankAccountId());
        return GetAccountRecipientsResult.of(
            accountRecipients.stream()
                .map(commandMapper::toAccountRecipient)
                .toList());
    }
}
