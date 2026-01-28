package com.jcondotta.account_recipients.infrastructure.adapters.output.cache;

import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
import com.jcondotta.account_recipients.infrastructure.adapters.output.metrics.CacheMetricsRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheStoreTest {

  private static final String CACHE_KEY = "cache:key";
  private static final String PREFIX = "cache";
  private static final String VALUE = "value";
  private static final Duration TTL = Duration.ofSeconds(60);

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @Mock
  private CacheMetricsRecorder metricsRecorder;

  private CacheStore<String> cacheStore;

  @BeforeEach
  void setUp() {
    cacheStore =
        new RedisCacheStore<>(
            redisTemplate,
            TTL,
            String.class,
            metricsRecorder);
  }

  // ---------------------------------------------------------------------------
  // put
  // ---------------------------------------------------------------------------

  @Test
  void shouldPutValueAndRecordMetric() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    cacheStore.put(CACHE_KEY, VALUE);

    verify(valueOperations).set(CACHE_KEY, VALUE, TTL);
    verify(metricsRecorder).recordPut();
    verifyNoMoreInteractions(metricsRecorder);
  }

  // ---------------------------------------------------------------------------
  // putIfAbsent
  // ---------------------------------------------------------------------------

  @Test
  void shouldPutIfAbsentAndRecordMetric_whenKeyIsAbsent() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(
        CACHE_KEY,
        VALUE,
        TTL.getSeconds(),
        TimeUnit.SECONDS))
        .thenReturn(true);

    cacheStore.putIfAbsent(CACHE_KEY, VALUE);

    verify(metricsRecorder).recordPut();
    verifyNoMoreInteractions(metricsRecorder);
  }

  @Test
  void shouldNotPutIfAbsentAndNotRecordMetric_whenKeyExists() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(
        CACHE_KEY,
        VALUE,
        TTL.getSeconds(),
        TimeUnit.SECONDS))
        .thenReturn(false);

    cacheStore.putIfAbsent(CACHE_KEY, VALUE);

    verifyNoInteractions(metricsRecorder);
  }

  // ---------------------------------------------------------------------------
  // getIfPresent
  // ---------------------------------------------------------------------------

  @Test
  void shouldReturnValueAndRecordHit_whenValueExists() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(CACHE_KEY)).thenReturn(VALUE);

    Optional<String> result = cacheStore.getIfPresent(CACHE_KEY);

    assertThat(result).contains(VALUE);
    verify(metricsRecorder).recordHit();
    verifyNoMoreInteractions(metricsRecorder);
  }

  @Test
  void shouldReturnEmptyAndRecordMiss_whenValueDoesNotExist() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(CACHE_KEY)).thenReturn(null);

    Optional<String> result = cacheStore.getIfPresent(CACHE_KEY);

    assertThat(result).isEmpty();
    verify(metricsRecorder).recordMiss();
    verifyNoMoreInteractions(metricsRecorder);
  }

  @Test
  void shouldReturnValueUsingDefaultGetMethod() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(CACHE_KEY)).thenReturn(VALUE);

    String result = cacheStore.get(CACHE_KEY);

    assertThat(result).isEqualTo(VALUE);
  }

  @Test
  void shouldReturnNullUsingDefaultGetMethod_whenValueIsAbsent() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(CACHE_KEY)).thenReturn(null);

    String result = cacheStore.get(CACHE_KEY);

    assertThat(result).isNull();
  }

  // ---------------------------------------------------------------------------
  // evict
  // ---------------------------------------------------------------------------

  @Test
  void shouldEvictKeySuccessfully() {
    when(redisTemplate.delete(CACHE_KEY)).thenReturn(true);

    boolean result = cacheStore.evict(CACHE_KEY);

    assertThat(result).isTrue();
    verify(redisTemplate).delete(CACHE_KEY);
  }

  @Test
  void shouldReturnFalseWhenEvictingNonExistingKey() {
    when(redisTemplate.delete(CACHE_KEY)).thenReturn(false);

    boolean result = cacheStore.evict(CACHE_KEY);

    assertThat(result).isFalse();
    verify(redisTemplate).delete(CACHE_KEY);
  }

  @Test
  void shouldReturnFalseWhenEvictThrowsException() {
    when(redisTemplate.delete(CACHE_KEY))
        .thenThrow(new RuntimeException("Redis error"));

    boolean result = cacheStore.evict(CACHE_KEY);

    assertThat(result).isFalse();
    verify(redisTemplate).delete(CACHE_KEY);
  }

  // ---------------------------------------------------------------------------
  // evictKeysByPrefix
  // ---------------------------------------------------------------------------

  @Test
  void shouldEvictKeysByPrefix_whenKeysExist() {
    Set<String> keys = Set.of("cache:1", "cache:2");
    when(redisTemplate.keys(PREFIX + ":*")).thenReturn(keys);

    cacheStore.evictKeysByPrefix(PREFIX);

    verify(redisTemplate).keys(PREFIX + ":*");
    verify(redisTemplate).delete(keys);
  }

  @Test
  void shouldDoNothing_whenNoKeysFoundByPrefix() {
    when(redisTemplate.keys(PREFIX + ":*")).thenReturn(Set.of());

    cacheStore.evictKeysByPrefix(PREFIX);

    verify(redisTemplate).keys(PREFIX + ":*");
    verify(redisTemplate, never()).delete(any(Set.class));
  }

  @Test
  void shouldSwallowException_whenEvictKeysByPrefixFails() {
    when(redisTemplate.keys(PREFIX + ":*"))
        .thenThrow(new RuntimeException("Redis error"));

    cacheStore.evictKeysByPrefix(PREFIX);

    verify(redisTemplate).keys(PREFIX + ":*");
    verify(redisTemplate, never()).delete(any(Set.class));
  }
}
