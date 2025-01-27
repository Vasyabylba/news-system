package ru.clevertec.newssystem.comment.cache.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LRUCacheTest {

    private LRUCache<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new LRUCache<>(3);
    }

    @Test
    void testPutAndGet() {
        // given
        String key = "key1";
        String value = "value1";

        // when
        cache.put(key, value);
        String retrievedValue = cache.get(key);

        // then
        assertEquals(value, retrievedValue, "The retrieved value should match the inserted value");
    }

    @Test
    void testEvictionPolicy() {
        // given
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        cache.get("key1");

        // when
        cache.put("key4", "value4");

        // then
        assertNull(cache.get("key2"), "The least recently used key should be evicted");
        assertNotNull(cache.get("key1"), "The recently accessed key should not be evicted");
        assertNotNull(cache.get("key3"), "Other keys should remain if they are not least recently used");
        assertNotNull(cache.get("key4"), "The newly added key should be present");
    }

    @Test
    void testDelete() {
        // given
        cache.put("key1", "value1");

        // when
        cache.delete("key1");

        // then
        assertFalse(cache.contains("key1"), "The deleted key should not be present in the cache");
        assertNull(cache.get("key1"), "Getting a deleted key should return null");
    }

    @Test
    void testContains() {
        // given
        cache.put("key1", "value1");

        // when
        boolean containsKey = cache.contains("key1");

        // then
        assertTrue(containsKey, "The cache should contain the inserted key");

        // when
        cache.delete("key1");
        boolean doesNotContainKey = cache.contains("key1");

        // then
        assertFalse(doesNotContainKey, "The cache should not contain the deleted key");
    }

    @Test
    void testCapacityLimit() {
        // given
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        cache.put("key4", "value4");

        // then
        assertTrue(cache.contains("key4"), "The most recent key should be in the cache");
        assertTrue(cache.contains("key3"), "Key3 should still be in the cache");
        assertFalse(cache.contains("key1"), "Key1 should have been evicted as the least recently used key");
        assertEquals(3, getCacheSize(), "Cache size should not exceed its capacity");
    }

    private int getCacheSize() {
        return cache.getCache().size();
    }

}