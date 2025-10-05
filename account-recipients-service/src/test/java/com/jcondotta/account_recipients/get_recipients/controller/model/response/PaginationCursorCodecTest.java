package com.jcondotta.account_recipients.get_recipients.controller.model.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.GetRecipientsLastEvaluatedKey;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link PaginationCursorCodec}.
 */
class PaginationCursorCodecTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID ACCOUNT_RECIPIENT_ID = UUID.randomUUID();
    private static final String RECIPIENT_NAME = "Jefferson Condotta";

    private static final GetRecipientsLastEvaluatedKey VALID_KEY =
        GetRecipientsLastEvaluatedKey.of(BANK_ACCOUNT_ID, ACCOUNT_RECIPIENT_ID, RECIPIENT_NAME);

    @Test
    void shouldEncodeToBase64_whenKeyIsValid() {
        var encoded = PaginationCursorCodec.encode(VALID_KEY);

        assertThat(encoded).isNotNull().isNotBlank();

        var decodedBytes = Base64.getUrlDecoder().decode(encoded);
        var json = new String(decodedBytes, StandardCharsets.UTF_8);

        assertThat(json)
            .contains(BANK_ACCOUNT_ID.toString())
            .contains(ACCOUNT_RECIPIENT_ID.toString())
            .contains(RECIPIENT_NAME);
    }

    @Test
    void shouldReturnNull_whenKeyIsNull() {
        assertThat(PaginationCursorCodec.encode(null)).isNull();
    }

    @Test
    void shouldThrowRuntimeException_whenEncodingFails() {
        // given – mock que causa falha de serialização
        var invalidKey = Mockito.mock(GetRecipientsLastEvaluatedKey.class);
        Mockito.when(invalidKey.bankAccountId()).thenThrow(new RuntimeException("boom"));

        // when / then
        assertThatThrownBy(() -> PaginationCursorCodec.encode(invalidKey))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to encode cursor");
    }

    @Test
    void shouldReturnEmptyOptional_whenEncodedIsNull() {
        assertThat(PaginationCursorCodec.decode(null)).isEmpty();
    }

    @Test
    void shouldThrowRuntimeException_whenEncodedIsBlank() {
        assertThatThrownBy(() -> PaginationCursorCodec.decode(" "))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Cursor is blank");
    }

    @Test
    void shouldThrowRuntimeException_whenEncodedIsNotBase64() {
        assertThatThrownBy(() -> PaginationCursorCodec.decode("###not-base64###"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Cursor is not valid Base64");
    }

    @Test
    void shouldDecodeToValidKey_whenEncodedIsValidBase64() throws Exception {
        byte[] bytes = MAPPER.writeValueAsBytes(VALID_KEY);
        var encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        Optional<GetRecipientsLastEvaluatedKey> decoded = PaginationCursorCodec.decode(encoded);

        assertThat(decoded).isPresent();
        assertThat(decoded.get())
            .usingRecursiveComparison()
            .isEqualTo(VALID_KEY);
    }

    @Test
    void shouldThrowRuntimeException_whenJsonIsInvalid() {
        var invalidJson = "{invalid-json}";
        var encoded = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(invalidJson.getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> PaginationCursorCodec.decode(encoded))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Cursor could not be deserialized");
    }

    @Test
    void shouldThrowRuntimeException_whenDecodedCursorIsNull() {
        var encoded = Base64.getUrlEncoder().withoutPadding()
            .encodeToString("null".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> PaginationCursorCodec.decode(encoded))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Cursor could not be deserialized");
    }
}
