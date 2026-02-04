package com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.request;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record GetRecipientsRestRequestParams(
    Integer limit, String namePrefix, String cursor) {

  public static GetRecipientsRestRequestParams of(Integer limit, String namePrefix, String cursor) {
    return new GetRecipientsRestRequestParams(limit, namePrefix, cursor);
  }

  public static GetRecipientsRestRequestParams of(Integer limit, String cursor) {
    return new GetRecipientsRestRequestParams(limit, null, cursor);
  }

  public static GetRecipientsRestRequestParams of(Integer limit) {
    return new GetRecipientsRestRequestParams(limit, null, null);
  }

  public String toSHA256Hex() {
    String raw = String.join("|",
        Objects.toString(limit, ""),
        Objects.toString(cursor, "")
    );

    return DigestUtils.sha256Hex(raw.getBytes(StandardCharsets.UTF_8));
  }
}
