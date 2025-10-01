package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_query.model.request;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record GetAccountRecipientsRestRequestParams(Integer limit, String cursor) {

    public static final class QueryParamsBuilder {
        private Integer limit;
        private String cursor;

        public QueryParamsBuilder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public QueryParamsBuilder withCursor(String cursor) {
            this.cursor = cursor;
            return this;
        }

        public GetAccountRecipientsRestRequestParams build() {
            return new GetAccountRecipientsRestRequestParams(limit, cursor);
        }
    }

    public static QueryParamsBuilder builder() {
        return new QueryParamsBuilder();
    }

    public String toSHA256Hex() {
        String raw = String.join("|",
            Objects.toString(limit, ""),
            Objects.toString(cursor, "")
        );

        return DigestUtils.sha256Hex(raw.getBytes(StandardCharsets.UTF_8));
    }
}
