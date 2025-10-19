package com.jcondotta.account_recipients.common.container;

import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class RedisTestContainer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String REDIS_IMAGE_NAME = "redis:7.4.3";
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse(REDIS_IMAGE_NAME);

    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(REDIS_IMAGE)
            .withReuse(true);

    private static final AtomicBoolean CONTAINER_STARTED = new AtomicBoolean(false);

    private static void startContainer() {
        try {
            Startables.deepStart(REDIS_CONTAINER).join();
            log.info("Redis container started on {}:{}", REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getFirstMappedPort());
        }
        catch (Exception e) {
            log.error("Failed to start Redis container: {}", e.getMessage());
            throw new RuntimeException("Failed to start Redis container", e);
        }
    }

    private static Map<String, String> getContainerProperties() {
        return Map.of(
                "REDIS_HOST", REDIS_CONTAINER.getHost(),
                "REDIS_PORT", String.valueOf(REDIS_CONTAINER.getFirstMappedPort())
        );
    }

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        if (CONTAINER_STARTED.compareAndSet(false, true)) {
            startContainer();
            TestPropertyValues.of(getContainerProperties()).applyTo(applicationContext.getEnvironment());
        } else {
            log.warn("initialize() called multiple times; Redis container already started.");
        }
    }
}
