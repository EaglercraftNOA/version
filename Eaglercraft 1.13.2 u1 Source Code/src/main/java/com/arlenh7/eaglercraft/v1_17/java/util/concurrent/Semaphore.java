package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.concurrent.TimeUnit;

public class Semaphore {
   private int permits;

   public Semaphore(int permits) {
      this.permits = permits;
   }

   public Semaphore(int permits, boolean fair) {
      this(permits);
   }

   public synchronized void acquire() throws InterruptedException {
      acquire(1);
   }

   public synchronized void acquire(int permits) throws InterruptedException {
      if (this.permits < permits) {
         throw new IllegalStateException("Blocking Semaphore.acquire() is not available in the browser runtime");
      }
      this.permits -= permits;
   }

   public synchronized boolean tryAcquire() {
      return tryAcquire(1);
   }

   public synchronized boolean tryAcquire(int permits) {
      if (this.permits >= permits) {
         this.permits -= permits;
         return true;
      }
      return false;
   }

   public synchronized boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
      return tryAcquire(1, timeout, unit);
   }

   public synchronized boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
      if (this.permits < permits) {
         return false;
      }
      this.permits -= permits;
      return true;
   }

   public synchronized void release() {
      release(1);
   }

   public synchronized void release(int permits) {
      this.permits += permits;
   }

   public synchronized int availablePermits() {
      return this.permits;
   }
}
