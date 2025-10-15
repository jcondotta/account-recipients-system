package com.jcondotta.account_recipients.get_recipients.controller.model.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.GetRecipientsLastEvaluatedKey;

import java.util.Base64;
import java.util.Optional;

public final class PaginationCursorCodec {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private PaginationCursorCodec() {
        // utility
    }

    public static String encode(GetRecipientsLastEvaluatedKey key) {
        if (key == null) return null;
        try {
            var json = MAPPER.writeValueAsBytes(key);
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode cursor", e);
        }
    }

    public static Optional<GetRecipientsLastEvaluatedKey> decode(String encoded) {
        if (encoded == null) {
            return Optional.empty();
        }
        if( encoded.isBlank()) {
            throw new IllegalArgumentException("Cursor is blank");
        }
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(encoded);
            var decoded = MAPPER.readValue(bytes, new TypeReference<GetRecipientsLastEvaluatedKey>() {});
            if (decoded == null) {
                throw new RuntimeException("Decoded cursor is null");
            }
            return Optional.of(decoded);
        } catch (IllegalArgumentException e) {
            // Base64 error
            throw new IllegalArgumentException("Cursor is not valid Base64", e);
        } catch (Exception e) {
            // JSON parse error
            throw new IllegalArgumentException("Cursor could not be deserialized", e);
        }
    }
}
