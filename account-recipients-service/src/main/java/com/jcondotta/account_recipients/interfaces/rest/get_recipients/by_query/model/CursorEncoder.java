package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_query.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.get_recipients.model.LastEvaluatedKey;

import java.util.Base64;

public final class CursorEncoder {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private CursorEncoder() {}

    public static String encode(LastEvaluatedKey key) {
        if (key == null) return null;
        try {
            var json = MAPPER.writeValueAsBytes(key);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception e) {
            throw new RuntimeException("Error encoding cursor", e);
        }
    }

    public static LastEvaluatedKey decode(String encoded) {
        if (encoded == null || encoded.isBlank()) return null;
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(encoded);
            return MAPPER.readValue(bytes, new TypeReference<LastEvaluatedKey>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error decoding cursor", e);
        }
    }
}
