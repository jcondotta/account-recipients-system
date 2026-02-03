package com.jcondotta.recipients.infrastructure.interfaces.rest.headers;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class HttpHeadersCustom {

  public static final String IDEMPOTENCY_KEY = "X-Idempotency-Key";
}
