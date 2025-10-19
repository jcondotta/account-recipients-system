package com.jcondotta.account_recipients.infrastructure.adapters.output.cache;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Base genérica para caches instrumentados com Micrometer.
 * Mede hits, misses, puts, evictions, errors e latência.
 * 
 * Exemplo de uso:
 *   public class RedisCacheStore extends InstrumentedCacheStore<String, AccountRecipients> {
 *       protected Optional<AccountRecipients> doGet(String key) { ... }
 *       protected void doPut(String key, AccountRecipients value) { ... }
 *       protected void doEvict(String key) { ... }
 *   }
 */
@Slf4j
public abstract class InstrumentedCacheStore<K, V> {

    private final Counter hits;
    private final Counter misses;
    private final Counter puts;
    private final Counter evicts;
    private final Counter errors;
    private final Timer loadTimer;

    protected InstrumentedCacheStore(MeterRegistry meterRegistry, String cacheName) {
        this.hits = Counter.builder("cache_hits_total").tag("cache", cacheName).register(meterRegistry);
        this.misses = Counter.builder("cache_misses_total").tag("cache", cacheName).register(meterRegistry);
        this.puts = Counter.builder("cache_puts_total").tag("cache", cacheName).register(meterRegistry);
        this.evicts = Counter.builder("cache_evictions_total").tag("cache", cacheName).register(meterRegistry);
        this.errors = Counter.builder("cache_errors_total").tag("cache", cacheName).register(meterRegistry);
        this.loadTimer = Timer.builder("cache_load_duration_seconds")
                .tag("cache", cacheName)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    /** Implementações concretas definem a lógica real de acesso ao cache. */
    protected abstract Optional<V> doGet(K key);
    protected abstract void doPut(K key, V value);
    protected abstract void doEvict(K key);

    /** Consulta com contagem de hit/miss. */
    public Optional<V> get(K key) {
        try {
            Optional<V> result = doGet(key);
            if (result.isPresent()) {
                hits.increment();
                log.debug("Cache HIT for key={}", key);
            } else {
                misses.increment();
                log.debug("Cache MISS for key={}", key);
            }
            return result;
        } catch (Exception e) {
            errors.increment();
            log.warn("Cache GET error for key={}", key, e);
            return Optional.empty();
        }
    }

    /** Inserção com tempo e contagem. */
    public void put(K key, V value) {
        try {
            loadTimer.record(() -> doPut(key, value));
            puts.increment();
            log.debug("Cache PUT for key={}", key);
        } catch (Exception e) {
            errors.increment();
            log.warn("Cache PUT error for key={}", key, e);
        }
    }

    /** Remoção com contagem. */
    public void evict(K key) {
        try {
            doEvict(key);
            evicts.increment();
            log.debug("Cache EVICT for key={}", key);
        } catch (Exception e) {
            errors.increment();
            log.warn("Cache EVICT error for key={}", key, e);
        }
    }
}
