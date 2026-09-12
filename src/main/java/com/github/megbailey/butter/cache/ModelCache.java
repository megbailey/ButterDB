package com.github.megbailey.butter.cache;

import com.github.megbailey.butter.Model;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory TTL cache for hot find/relation lookups.
 */
public class ModelCache {
    private static final ModelCache INSTANCE = new ModelCache();

    private final Map<String, CacheEntry> store = new ConcurrentHashMap<>();
    private long defaultTtlMs = 60_000;

    public static ModelCache getInstance() {
        return INSTANCE;
    }

    public void setDefaultTtlMs(long ttlMs) {
        this.defaultTtlMs = ttlMs;
    }

    public void put(String key, Model model) {
        put(key, model, defaultTtlMs);
    }

    public void put(String key, Model model, long ttlMs) {
        store.put(key, new CacheEntry(model, System.currentTimeMillis() + ttlMs));
    }

    public Optional<Model> get(String key) {
        CacheEntry entry = store.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (System.currentTimeMillis() > entry.expiresAt) {
            store.remove(key);
            return Optional.empty();
        }
        return Optional.ofNullable(entry.model);
    }

    public void invalidate(String key) {
        store.remove(key);
    }

    public void invalidatePrefix(String prefix) {
        store.keySet().removeIf(k -> k.startsWith(prefix));
    }

    public void clear() {
        store.clear();
    }

    public static String key(String table, Object pk) {
        return table + ":" + pk;
    }

    private static final class CacheEntry {
        private final Model model;
        private final long expiresAt;

        private CacheEntry(Model model, long expiresAt) {
            this.model = model;
            this.expiresAt = expiresAt;
        }
    }
}
