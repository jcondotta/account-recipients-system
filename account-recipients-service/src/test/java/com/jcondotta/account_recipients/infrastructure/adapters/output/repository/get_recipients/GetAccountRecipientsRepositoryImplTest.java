package com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetAccountRecipientsRepositoryImplTest {

//    @Nested
//    class BuildQueryConditional {
//
//        @Test
//        void shouldBuildKeyEqualToConditional_whenNamePrefixIsNull() {
//            // given
//            var bankAccountId = BankAccountId.of(UUID.randomUUID());
//            var queryParams = new GetAccountRecipientsQueryParams(null, null, null);
//            var query = new GetAccountRecipientsQuery(bankAccountId, queryParams);
//
//            // when
//            QueryConditional conditional = repository.buildQueryConditional(query);
//
//            // then
//            var expectedPartitionKey = AccountRecipientEntityKey.partitionKey(bankAccountId);
//            assertThat(conditional.toString())
//                .contains("KeyEqualTo")
//                .contains(expectedPartitionKey);
//        }
//
//        @Test
//        void shouldBuildSortBeginsWithConditional_whenNamePrefixIsPresent() {
//            // given
//            var bankAccountId = BankAccountId.of(UUID.randomUUID());
//            var namePrefix = new RecipientNamePrefix("Jeff");
//            var queryParams = new GetAccountRecipientsQueryParams(null, null, namePrefix);
//            var query = new GetAccountRecipientsQuery(bankAccountId, queryParams);
//
//            // when
//            QueryConditional conditional = repository.buildQueryConditional(query);
//
//            // then
//            var expectedPartitionKey = AccountRecipientEntityKey.partitionKey(bankAccountId);
//            assertThat(conditional.toString())
//                .contains("SortBeginsWith")
//                .contains(expectedPartitionKey)
//                .contains(namePrefix.value());
//        }
//
//    }

}