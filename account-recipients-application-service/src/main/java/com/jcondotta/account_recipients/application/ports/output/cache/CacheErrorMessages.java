package com.jcondotta.account_recipients.application.ports.output.cache;

public final class CacheErrorMessages {

    public static final String CACHE_MUST_NOT_BE_NULL = "cache.notNull";
    public static final String KEY_MUST_NOT_BE_NULL = "cache.key.notNull";

    public static final String VALUE_MUST_NOT_BE_NULL = "cache.value.notNull";
    public static final String TIME_UNIT_MUST_NOT_BE_NULL = "cache.timeUnit.notNull";
    public static final String VALUE_SUPPLIER_MUST_NOT_BE_NULL = "cache.valueSupplier.notNull";

    public static final String DEFAULT_TTL_MUST_NOT_BE_NULL = "cache.default.ttl.notNull";
    public static final String TTL_MUST_NOT_BE_NULL = "cache.ttl.notNull";
    public static final String TTL_MUST_BE_POSITIVE = "cache.ttl.positive";

    private CacheErrorMessages() {}
}
