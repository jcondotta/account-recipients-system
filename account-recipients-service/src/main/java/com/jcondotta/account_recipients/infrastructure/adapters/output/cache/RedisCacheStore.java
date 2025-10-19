package com.jcondotta.account_recipients.infrastructure.adapters.output.cache;

import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
import com.jcondotta.account_recipients.infrastructure.adapters.output.metrics.CacheMetricsRecorder;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisCacheStore<V> implements CacheStore<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheStore.class);

    private final RedisTemplate<String, V> redisTemplate;
    private final Duration defaultTimeToLive;
    private final Class<V> valueClass;
    private final CacheMetricsRecorder metricsRecorder;

    public RedisCacheStore(RedisTemplate<String, V> redisTemplate, Duration defaultTimeToLive, Class<V> valueClass, CacheMetricsRecorder metricsRecorder) {
        this.redisTemplate = redisTemplate;
        this.defaultTimeToLive = defaultTimeToLive;
        this.valueClass = valueClass;
        this.metricsRecorder = metricsRecorder;
    }

    @Observed(
        name = "cache.put",
        contextualName = "redisPutCacheValue",
        lowCardinalityKeyValues = {
            "operation", "put",
            "cacheType", "redis"
        }
    )
    @Override
    public void put(String cacheKey, V cacheValue) {
        LOGGER.debug("Adding cache entry: key='{}', value='{}'", cacheKey, cacheValue);

        redisTemplate
            .opsForValue()
            .set(cacheKey, cacheValue, defaultTimeToLive);
        metricsRecorder.recordPut();

        LOGGER.debug("Cache put: key='{}' successfully stored.", cacheKey);
    }

    @Observed(
        name = "cache.put_if_absent",
        contextualName = "redisPutIfAbsentCacheValue",
        lowCardinalityKeyValues = {
            "operation", "putIfAbsent",
            "cacheType", "redis"
        }
    )
    @Override
    public void putIfAbsent(String cacheKey, V cacheValue) {
        LOGGER.trace("Attempting to set cache entry for key='{}' if absent.", cacheKey);

        Boolean wasEntrySet = redisTemplate
            .opsForValue()
            .setIfAbsent(cacheKey, cacheValue, defaultTimeToLive.getSeconds(), TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(wasEntrySet)) {
            metricsRecorder.recordPut();
            LOGGER.debug("Cache entry set for key='{}' as it was absent.", cacheKey);
        }
        else {
            LOGGER.trace("Cache entry for key='{}' already existed; value was not updated.", cacheKey);
        }
    }

    @Observed(
        name = "cache.get",
        contextualName = "redisGetCacheValue",
        lowCardinalityKeyValues = {
            "operation", "get",
            "cacheType", "redis"
        }
    )
    @Override
    public Optional<V> getIfPresent(String cacheKey) {
        V value = redisTemplate.opsForValue().get(cacheKey);
        if (value != null) {
            LOGGER.debug("Cache hit: key='{}'", cacheKey);
            metricsRecorder.recordHit();
            if (valueClass.isInstance(value)) {
                return Optional.of(valueClass.cast(value));
            }
            else {
                LOGGER.warn("Cache key='{}' has unexpected type: {}", cacheKey, value.getClass().getName());
                return Optional.empty();
            }
        }

        LOGGER.info("Cache miss: key='{}'", cacheKey);
        metricsRecorder.recordMiss();
        return Optional.empty();
    }

    @Override
    public boolean evict(String cacheKey) {
        try {
            Boolean deleted = redisTemplate.delete(cacheKey);

            if (deleted) {
                LOGGER.debug("Cache evicted successfully for key='{}'", cacheKey);
            } else {
                LOGGER.debug("Cache eviction skipped — key='{}' was not found", cacheKey);
            }

            return deleted;
        } catch (Exception e) {
            LOGGER.warn("Failed to evict cache for key='{}'", cacheKey, e);
            return false;
        }
    }

    @Override
    public void evictKeysByPrefix(String prefixCacheKey) {
        try {
            // Cria o pattern para o Redis (ex: "accountRecipients::3d5e3489:*")
            String pattern = prefixCacheKey + ":*";

            long startTime = System.currentTimeMillis();
            Set<String> keys = redisTemplate.keys(pattern);

            if (keys == null || keys.isEmpty()) {
                LOGGER.debug("No cache keys found with prefix='{}'", prefixCacheKey);
                return;
            }

            // Remove todas as chaves encontradas
            redisTemplate.delete(keys);

            long durationMs = System.currentTimeMillis() - startTime;
            LOGGER.debug("Evicted {} cache keys with prefix='{}' in {} ms",
                keys.size(), prefixCacheKey, durationMs);

        } catch (Exception e) {
            LOGGER.warn("Failed to evict cache keys with prefix='{}'", prefixCacheKey, e);
        }
    }
}
