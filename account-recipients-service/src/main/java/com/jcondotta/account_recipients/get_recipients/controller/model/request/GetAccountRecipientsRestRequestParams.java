package com.jcondotta.account_recipients.get_recipients.controller.model.request;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record GetAccountRecipientsRestRequestParams(
    Integer limit, String namePrefix, String cursor) {

  public static GetAccountRecipientsRestRequestParams of(Integer limit, String namePrefix, String cursor) {
    return new GetAccountRecipientsRestRequestParams(limit, namePrefix, cursor);
  }

  public static GetAccountRecipientsRestRequestParams of(Integer limit, String cursor) {
    return new GetAccountRecipientsRestRequestParams(limit, null, cursor);
  }

  public static GetAccountRecipientsRestRequestParams of(Integer limit) {
    return new GetAccountRecipientsRestRequestParams(limit, null, null);
  }

  public String toSHA256Hex() {
    String raw = String.join("|",
        Objects.toString(limit, ""),
        Objects.toString(cursor, "")
    );

    return DigestUtils.sha256Hex(raw.getBytes(StandardCharsets.UTF_8));
  }
}
