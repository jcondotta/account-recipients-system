package com.jcondotta.account_recipients.application.ports.output.cache;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CacheStoreTest {

    @Test
    void shouldReturnValueFromDefaultGet_whenValuePresent() {
        CacheStore<String> cacheStore = new CacheStore<>() {
            @Override
            public void put(String cacheKey, String cacheValue) {}

            @Override
            public void putIfAbsent(String cacheKey, String cacheValue) {}

            @Override
            public Optional<String> getIfPresent(String cacheKey) {
                return Optional.of("cached-value");
            }

            @Override
            public boolean evict(String cacheKey) { return false; }

            @Override
            public void evictKeysByPrefix(String prefixCacheKey) {}
        };

        var result = cacheStore.get("any-key");
        assertThat(result).isEqualTo("cached-value");
    }

    @Test
    void shouldReturnNullFromDefaultGet_whenValueNotPresent() {
        CacheStore<String> cacheStore = new CacheStore<>() {
            @Override
            public void put(String cacheKey, String cacheValue) {}

            @Override
            public void putIfAbsent(String cacheKey, String cacheValue) {}

            @Override
            public Optional<String> getIfPresent(String cacheKey) {
                return Optional.empty();
            }

            @Override
            public boolean evict(String cacheKey) { return false; }

            @Override
            public void evictKeysByPrefix(String prefixCacheKey) {}
        };

        var result = cacheStore.get("any-key");
        assertThat(result).isNull();
    }
}
