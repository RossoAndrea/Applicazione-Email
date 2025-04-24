package com.example.server.locks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

public class Locks<T> {

    private final ConcurrentHashMap<T, AtomicLock> locks;

    public Locks() {
        locks = new ConcurrentHashMap<>();
    }

    /**
     * Create the lock
     * @param key lock id
     */
    public void createLock(T key) {
        AtomicLock atomicLock = locks.getOrDefault(key, new AtomicLock());
        atomicLock.getOccurrences().incrementAndGet();
        locks.putIfAbsent(key, atomicLock);
    }

    /**
     * Get the read lock
     * @param key lock id
     * @return lock
     */
    public Lock getWriteLock(T key) {
        AtomicLock atomicLock = locks.get(key);
        if (atomicLock == null) throw new NullPointerException("The requested lock does not exist");

        return atomicLock.writeLock();
    }

    /**
     * Get the read lock
     * @param key lock id
     * @return lock
     */
    public Lock getReadLock(T key) {
        AtomicLock atomicLock = locks.get(key);
        if (atomicLock == null) throw new NullPointerException("The requested lock does not exist");

        return atomicLock.readLock();
    }

    /**
     * Delete the lock
     * @param key id
     */
    public void deleteLock(T key) {
        AtomicLock atomicLock = locks.get(key);
        if (atomicLock == null) throw new NullPointerException("The lock to delete does not exist");

        Lock writeLock = atomicLock.writeLock();
        if (atomicLock.getOccurrences().decrementAndGet() == 0) {
            locks.remove(key);
        }
    }
}
