package ru.clevertec.newssystem.news.cache.impl;

import lombok.Getter;
import ru.clevertec.newssystem.news.cache.Cache;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class LRUCache<K, V> implements Cache<K, V> {

    private final Map<K, V> cache;

    public LRUCache(int capacity) {
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public synchronized V get(K key) {
        return cache.getOrDefault(key, null);
    }

    @Override
    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public synchronized void delete(K key) {
        cache.remove(key);
    }

    @Override
    public boolean contains(K key) {
        return cache.containsKey(key);
    }

}
