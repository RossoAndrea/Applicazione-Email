package com.example.server.locks;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
Questa è una classe privata e statica che estende ReentrantReadWriteLock e aggiunge un contatore
AtomicInteger occurrences, che tiene traccia di quante volte un lock viene usato. La classe è
progettata per garantire che ogni chiave possa essere bloccata in modo sicuro e che la rimozione
del lock avvenga solo quando non ci sono più thread che lo utilizzano.

ReentrantReadWriteLock è una classe che fornisce due tipi di lock:

Lock in lettura (readLock()):
Permette a più thread di leggere la risorsa condivisa contemporaneamente, purché nessun thread stia
scrivendo. Questo è utile quando i thread leggono spesso ma scrivono raramente, migliorando le performance.

Lock in scrittura (writeLock()):
Permette a un solo thread di scrivere la risorsa condivisa alla volta, impedendo a qualsiasi altro
thread di leggere o scrivere. Il lock in scrittura è esclusivo.

 */
public final class AtomicLock extends ReentrantReadWriteLock {
    private final AtomicInteger occurrences = new AtomicInteger();

    public AtomicInteger getOccurrences() {
        return occurrences;
    }
}
