package com.jcondotta.account_recipients.infrastructure.adapters.output.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CacheMetricsRecorder {

    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Counter cachePutCounter;

    public CacheMetricsRecorder(MeterRegistry meterRegistry) {
        this.cacheHitCounter = Counter.builder("cache_hits_total")
                .description("Total number of cache hits")
                .tag("cacheType", "redis")
                .register(meterRegistry);

        this.cacheMissCounter = Counter.builder("cache_misses_total")
                .description("Total number of cache misses")
                .tag("cacheType", "redis")
                .register(meterRegistry);

        this.cachePutCounter = Counter.builder("cache_puts_total")
                .description("Total number of cache put operations")
                .tag("cacheType", "redis")
                .register(meterRegistry);
    }

    public void recordHit() {
        cacheHitCounter.increment();
    }

    public void recordMiss() {
        cacheMissCounter.increment();
    }

    public void recordPut() {
        cachePutCounter.increment();
    }
}
