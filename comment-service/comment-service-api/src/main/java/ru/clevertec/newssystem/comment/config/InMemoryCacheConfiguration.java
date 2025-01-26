package ru.clevertec.newssystem.comment.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.cache.Cache;
import ru.clevertec.newssystem.comment.cache.impl.LFUCache;
import ru.clevertec.newssystem.comment.cache.impl.LRUCache;

/**
 * Configuration class for configuring in memory cache.
 * Configures the cache type (LRU or LFU) based on the `in-memory-cache` property.
 */
@Configuration
@ConditionalOnProperty(value = "in-memory-cache.enable", havingValue = "true")
@EnableConfigurationProperties(InMemoryCacheProperties.class)
public class InMemoryCacheConfiguration {

    private static final int DEFAULT_CACHE_CAPACITY = 3;

    @Bean
    @ConditionalOnProperty(value = "in-memory-cache.algorithm", havingValue = "lru", matchIfMissing = true)
    public Cache<String, CommentEntity> lruCommentEntityCache(InMemoryCacheProperties cacheProperties) {
        if (cacheProperties.getCapacity() == null) {
            return new LRUCache<>(DEFAULT_CACHE_CAPACITY);
        }
        return new LRUCache<>(cacheProperties.getCapacity());
    }

    @Bean
    @ConditionalOnProperty(value = "in-memory-cache.algorithm", havingValue = "lfu")
    public Cache<String, CommentEntity> lfuCommentEntityCache(InMemoryCacheProperties cacheProperties) {
        if (cacheProperties.getCapacity() == null) {
            return new LFUCache<>(DEFAULT_CACHE_CAPACITY);
        }
        return new LFUCache<>(cacheProperties.getCapacity());
    }

}
