package com.jcondotta.account_recipients.application.ports.output.cache;

import java.util.Optional;

public interface CacheStore<V> {

    void put(String cacheKey, V cacheValue);

    void putIfAbsent(String cacheKey, V cacheValue);

    Optional<V> getIfPresent(String cacheKey);

    boolean evict(String cacheKey);
    void evictKeysByPrefix(String prefixCacheKey);

    default V get(String cacheKey) {
        return getIfPresent(cacheKey).orElse(null);
    }
}