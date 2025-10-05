package com.jcondotta.account_recipients.get_recipients.controller.model.request;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record GetAccountRecipientsRestRequestParams(Integer limit, String cursor) {

    public static GetAccountRecipientsRestRequestParams of(Integer limit, String cursor) {
        return new GetAccountRecipientsRestRequestParams(limit, cursor);
    }

    public String toSHA256Hex() {
        String raw = String.join("|",
            Objects.toString(limit, ""),
            Objects.toString(cursor, "")
        );

        return DigestUtils.sha256Hex(raw.getBytes(StandardCharsets.UTF_8));
    }
}
