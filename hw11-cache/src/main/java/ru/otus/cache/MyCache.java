package ru.otus.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(MyCache.class);
    private final List<HwListener<K, V>> listeners = new ArrayList<>();
    private final WeakHashMap<K, V> cache = new WeakHashMap<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        listeners.forEach(l -> l.notify(key, value, "put"));
    }

    @Override
    public void remove(K key) {
        V value = cache.get(key);
        cache.remove(key);
        listeners.forEach(l -> l.notify(key, value, "remove"));
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        listeners.forEach(l -> l.notify(key, value, "get"));
        return cache.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }
}
