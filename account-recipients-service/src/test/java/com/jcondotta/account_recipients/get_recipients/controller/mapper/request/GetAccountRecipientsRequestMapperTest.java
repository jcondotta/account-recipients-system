package com.jcondotta.account_recipients.get_recipients.controller.mapper.request;

import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetAccountRecipientsQuery;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.get_recipients.controller.model.request.GetAccountRecipientsRestRequestParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests focusing on null-safety branches of {@link GetAccountRecipientsRequestMapperImpl}.
 */
class GetAccountRecipientsRequestMapperImplTest {

    private GetAccountRecipientsRequestMapperImpl mapper;

    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mapper = new GetAccountRecipientsRequestMapperImpl();
    }

    @Test
    void shouldReturnNull_whenBankAccountIdAndRequestParamsAreNull() {
        GetAccountRecipientsQuery result = mapper.toQuery(null, null);

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnQueryWithNullQueryParams_whenOnlyBankAccountIdIsNull() {
        var requestParams = GetAccountRecipientsRestRequestParams.of(15, "cursor-123");
        assertThatThrownBy(() -> mapper.toQuery(null, requestParams))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("bank account id value must not be null.");
    }


    @Test
    void shouldReturnQueryWithNullQueryParams_whenOnlyRequestParamsIsNull() {
        assertThatThrownBy(() -> mapper.toQuery(BANK_ACCOUNT_UUID, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("queryParams must not be null");
    }

    @Test
    void shouldReturnQueryWithPopulatedQueryParams_whenRequestParamsIsProvided() {
        // given
        var requestParams = GetAccountRecipientsRestRequestParams.of(15, "cursor-123");

        // when
        var result = mapper.toQuery(BANK_ACCOUNT_UUID, requestParams);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(it -> {
                assertThat(it.bankAccountId()).isEqualTo(BankAccountId.of(BANK_ACCOUNT_UUID));
                GetAccountRecipientsQueryParams params = it.queryParams();
                assertThat(params.limit()).isEqualTo(15);
                assertThat(params.cursor()).isEqualTo("cursor-123");
            });
    }
}
