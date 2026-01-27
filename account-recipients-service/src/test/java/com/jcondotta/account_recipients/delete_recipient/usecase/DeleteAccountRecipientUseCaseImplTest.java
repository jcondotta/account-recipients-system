package com.jcondotta.account_recipients.delete_recipient.usecase;

import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
import com.jcondotta.account_recipients.application.ports.output.repository.delete_recipient.DeleteAccountRecipientRepository;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipient.GetAccountRecipientRepository;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DeleteAccountRecipientUseCaseImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
  private static final RecipientId ACCOUNT_RECIPIENT_ID = RecipientId.of(ACCOUNT_RECIPIENT_UUID);

  @Mock
  private GetAccountRecipientRepository getAccountRecipientRepository;

  @Mock
  private DeleteAccountRecipientRepository deleteAccountRecipientRepository;

  @Mock
  private CacheStore<GetAccountRecipientsResult> cacheStoreMock;

  @Captor
  private ArgumentCaptor<BankAccountId> bankAccountIdCaptor;

  @Captor
  private ArgumentCaptor<RecipientId> recipientIdCaptor;

  private DeleteAccountRecipientUseCaseImpl useCase;

  @BeforeEach
  void setUp() {
//    useCase = new DeleteAccountRecipientUseCaseImpl(getAccountRecipientRepository, deleteAccountRecipientRepository, cacheStoreMock);
  }

//  @Test
//  void shouldDeleteRecipient_whenCommandIsValid() {
//    var command = DeleteAccountRecipientCommand.of(BANK_ACCOUNT_ID, ACCOUNT_RECIPIENT_ID);
//
//    useCase.execute(command);
//
//    verify(deleteAccountRecipientRepository).delete(bankAccountIdCaptor.capture(), recipientIdCaptor.capture());
//    assertThat(bankAccountIdCaptor.getValue()).isEqualTo(BANK_ACCOUNT_ID);
//    assertThat(recipientIdCaptor.getValue()).isEqualTo(ACCOUNT_RECIPIENT_ID);
//
//    var cacheKey = AccountRecipientsRootCacheKey.of(BANK_ACCOUNT_ID);
//    verify(cacheStoreMock).evictKeysByPrefix(cacheKey.value());
//
//    verifyNoMoreInteractions(deleteAccountRecipientRepository, cacheStoreMock);
//  }

//  @Test
//  void shouldNotInteractWithRepository_whenCommandIsNull() {
//    assertThatThrownBy(() -> useCase.execute(null))
//        .isInstanceOf(NullPointerException.class)
//        .hasMessage("Command must not be null");
//
//    verifyNoInteractions(deleteAccountRecipientRepository, cacheStoreMock);
//  }
}
