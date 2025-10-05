package com.jcondotta.account_recipients.get_recipients.controller.model.request;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GetAccountRecipientsRestRequestParams}.
 */
class GetAccountRecipientsRestRequestParamsTest {

    private static final Integer LIMIT = 50;
    private static final String CURSOR = "abc123cursor";

    @Test
    void shouldCreateRequestParams_whenAllFieldsAreProvided() {
        var params = GetAccountRecipientsRestRequestParams.of(LIMIT, CURSOR);

        assertThat(params)
            .satisfies(it -> {
                assertThat(it.limit()).isEqualTo(LIMIT);
                assertThat(it.cursor()).isEqualTo(CURSOR);
            });
    }

    @Test
    void shouldGenerateSHA256Hex_whenFieldsAreValid() {
        var params = GetAccountRecipientsRestRequestParams.of(LIMIT, CURSOR);

        var expectedRaw = String.join("|", LIMIT.toString(), CURSOR);
        var expectedHash = DigestUtils.sha256Hex(expectedRaw.getBytes(StandardCharsets.UTF_8));

        assertThat(params.toSHA256Hex()).isEqualTo(expectedHash);
    }

    @Test
    void shouldGenerateSHA256Hex_whenCursorIsNull() {
        var params = GetAccountRecipientsRestRequestParams.of(LIMIT, null);

        var expectedRaw = String.join("|", LIMIT.toString(), "");
        var expectedHash = DigestUtils.sha256Hex(expectedRaw.getBytes(StandardCharsets.UTF_8));

        assertThat(params.toSHA256Hex()).isEqualTo(expectedHash);
    }

    @Test
    void shouldGenerateSHA256Hex_whenLimitIsNull() {
        var params = GetAccountRecipientsRestRequestParams.of(null, CURSOR);

        var expectedRaw = String.join("|", "", CURSOR);
        var expectedHash = DigestUtils.sha256Hex(expectedRaw.getBytes(StandardCharsets.UTF_8));

        assertThat(params.toSHA256Hex()).isEqualTo(expectedHash);
    }

    @Test
    void shouldGenerateSHA256Hex_whenBothFieldsAreNull() {
        var params = GetAccountRecipientsRestRequestParams.of(null, null);

        var expectedRaw = String.join("|", "", "");
        var expectedHash = DigestUtils.sha256Hex(expectedRaw.getBytes(StandardCharsets.UTF_8));

        assertThat(params.toSHA256Hex()).isEqualTo(expectedHash);
    }

    @Test
    void shouldHaveValueEquality_whenFieldsAreIdentical() {
        var params1 = GetAccountRecipientsRestRequestParams.of(LIMIT, CURSOR);
        var params2 = GetAccountRecipientsRestRequestParams.of(LIMIT, CURSOR);

        assertThat(params1)
            .isEqualTo(params2)
            .hasSameHashCodeAs(params2);
    }

    @Test
    void shouldIncludeFieldValuesInToString() {
        var params = GetAccountRecipientsRestRequestParams.of(LIMIT, CURSOR);

        assertThat(params.toString())
            .contains(LIMIT.toString())
            .contains(CURSOR);
    }
}
