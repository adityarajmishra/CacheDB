package com.cachedb.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class CacheDB {
    private final Map<String, CacheEntry> cache;
    private final ReadWriteLock lock;
    private final ScheduledExecutorService scheduler;

    public CacheDB() {
        this.cache = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this::clearExpiredKeys, 1, 1, TimeUnit.SECONDS);
    }

    public void put(String key, CacheEntry entry) {
        lock.writeLock().lock();
        try {
            cache.put(key, entry);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public CacheEntry get(String key) {
        lock.readLock().lock();
        try {
            CacheEntry entry = cache.get(key);
            return (entry != null && !entry.isExpired()) ? entry : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void delete(String key) {
        lock.writeLock().lock();
        try {
            cache.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    private void clearExpiredKeys() {
        long now = System.currentTimeMillis();
        lock.writeLock().lock();
        try {
            cache.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
        } finally {
            lock.writeLock().unlock();
        }
    }
}