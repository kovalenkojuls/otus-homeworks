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
        notifyListeners(key, value, "put");;
    }

    @Override
    public void remove(K key) {
        V value = cache.get(key);
        cache.remove(key);
        notifyListeners(key, value, "remove");
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        notifyListeners(key, value, "get");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    void notifyListeners(K key, V value, String action) {
        for (int i = 0; i < listeners.size(); ++i) {
            try {
                listeners.get(i).notify(key, value, action);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
