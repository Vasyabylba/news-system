package ru.clevertec.newssystem.comment.cache.impl;

import lombok.Getter;
import ru.clevertec.newssystem.comment.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

@Getter
public class LFUCache<K, V> implements Cache<K, V> {

    private final Map<K, V> cache;

    private final Map<K, Integer> frequency;

    private final PriorityQueue<K> frequencyQueue;

    private final int capacity;

    public LFUCache(int capacity) {
        this.cache = new HashMap<>();
        this.frequency = new HashMap<>();
        this.frequencyQueue = new PriorityQueue<>((a, b) -> {
            int freqA = frequency.getOrDefault(a, 0);
            int freqB = frequency.getOrDefault(b, 0);
            return Integer.compare(freqA, freqB);
        });
        this.capacity = capacity;
    }

    @Override
    public synchronized V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        frequency.put(key, frequency.getOrDefault(key, 0) + 1);
        rebuildQueue();
        return cache.get(key);
    }

    @Override
    public synchronized void put(K key, V value) {
        if (cache.containsKey(key)) {
            cache.put(key, value);
            frequency.put(key, frequency.getOrDefault(key, 0) + 1);
            rebuildQueue();
            return;
        }

        if (cache.size() >= capacity) {
            K evictKey = frequencyQueue.poll();
            if (evictKey != null) {
                cache.remove(evictKey);
                frequency.remove(evictKey);
            }
        }

        cache.put(key, value);
        frequency.put(key, 1);
        rebuildQueue();
    }

    @Override
    public synchronized void delete(K key) {
        cache.remove(key);
        frequency.remove(key);
        rebuildQueue();
    }

    @Override
    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    private void rebuildQueue() {
        frequencyQueue.clear();
        frequencyQueue.addAll(cache.keySet());
    }

}
