package com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.get_recipients.model.GetRecipientsLastEvaluatedKey;
import lombok.experimental.UtilityClass;

import java.util.Base64;
import java.util.Optional;

@UtilityClass
public final class PaginationCursorCodec {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static String encode(GetRecipientsLastEvaluatedKey key) {
    if (key == null) return null;
    try {
      var json = MAPPER.writeValueAsBytes(key);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to encode cursor", e);
    }
  }

  public static Optional<GetRecipientsLastEvaluatedKey> decode(String encoded) {
    if (encoded == null) {
      return Optional.empty();
    }
    if (encoded.isBlank()) {
      throw new IllegalArgumentException("Cursor is blank");
    }
    try {
      byte[] bytes = Base64.getUrlDecoder().decode(encoded);
      var decoded = MAPPER.readValue(bytes, new TypeReference<GetRecipientsLastEvaluatedKey>() {
      });
      return Optional.of(decoded);
    } catch (IllegalArgumentException e) {
      // Base64 error
      throw new IllegalStateException("Cursor is not valid Base64", e);
    } catch (Exception e) {
      // JSON parse error
      throw new IllegalStateException("Cursor could not be deserialized", e);
    }
  }
}
