package com.arlenh7.eaglercraft.v1_17.java.util.concurrent.locks;

public interface ReadWriteLock {
   Lock readLock();

   Lock writeLock();
}
