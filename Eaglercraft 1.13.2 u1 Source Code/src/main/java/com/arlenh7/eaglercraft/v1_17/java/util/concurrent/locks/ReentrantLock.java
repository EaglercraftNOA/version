package com.arlenh7.eaglercraft.v1_17.java.util.concurrent.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReentrantLock implements Lock {
   private Thread owner;
   private int holdCount;
   private final boolean fair;

   public ReentrantLock() {
      this(false);
   }

   public ReentrantLock(boolean fair) {
      this.fair = fair;
   }

   @Override
   public synchronized void lock() {
      Thread thread = Thread.currentThread();
      if (this.owner != null && this.owner != thread) {
         throw new IllegalStateException("Lock contention is not supported in the browser runtime");
      }
      this.owner = thread;
      ++this.holdCount;
   }

   @Override
   public synchronized void lockInterruptibly() throws InterruptedException {
      Thread thread = Thread.currentThread();
      if (this.owner != null && this.owner != thread) {
         throw new IllegalStateException("Lock contention is not supported in the browser runtime");
      }
      this.owner = Thread.currentThread();
      ++this.holdCount;
   }

   @Override
   public synchronized boolean tryLock() {
      Thread thread = Thread.currentThread();
      if (this.owner != null && this.owner != thread) {
         return false;
      }
      this.owner = thread;
      ++this.holdCount;
      return true;
   }

   @Override
   public synchronized boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
      Thread thread = Thread.currentThread();
      if (this.owner != null && this.owner != thread) {
         return false;
      }
      this.owner = thread;
      ++this.holdCount;
      return true;
   }

   @Override
   public synchronized void unlock() {
      if (this.owner != Thread.currentThread()) {
         throw new IllegalMonitorStateException();
      }
      if (this.holdCount > 0) {
         --this.holdCount;
         if (this.holdCount == 0) {
            this.owner = null;
         }
      }
   }

   @Override
   public Object newCondition() {
      return new SimpleCondition();
   }

   public synchronized boolean isHeldByCurrentThread() {
      return this.owner == Thread.currentThread();
   }

   public synchronized int getHoldCount() {
      return isHeldByCurrentThread() ? this.holdCount : 0;
   }

   public synchronized boolean isLocked() {
      return this.owner != null;
   }

   public boolean isFair() {
      return this.fair;
   }

   public synchronized boolean hasQueuedThreads() {
      return false;
   }

   public synchronized boolean hasQueuedThread(Thread thread) {
      return false;
   }

   public synchronized int getQueueLength() {
      return 0;
   }

   private synchronized int fullyUnlock() {
      if (this.owner != Thread.currentThread()) {
         throw new IllegalMonitorStateException();
      }
      int savedHoldCount = this.holdCount;
      this.holdCount = 0;
      this.owner = null;
      return savedHoldCount;
   }

   private void relock(int savedHoldCount) {
      lock();
      synchronized (this) {
         this.holdCount = savedHoldCount;
      }
   }

   private final class SimpleCondition {
      public void await() throws InterruptedException {
         int savedHoldCount = fullyUnlock();
         try {
         } finally {
            relock(savedHoldCount);
         }
      }

      public void awaitUninterruptibly() {
         int savedHoldCount = fullyUnlock();
         try {
         } finally {
            relock(savedHoldCount);
         }
      }

      public long awaitNanos(long nanosTimeout) throws InterruptedException {
         long start = System.nanoTime();
         int savedHoldCount = fullyUnlock();
         try {
            return nanosTimeout - (System.nanoTime() - start);
         } finally {
            relock(savedHoldCount);
         }
      }

      public boolean await(long time, TimeUnit unit) throws InterruptedException {
         int savedHoldCount = fullyUnlock();
         try {
            return true;
         } finally {
            relock(savedHoldCount);
         }
      }

      public boolean awaitUntil(Date deadline) throws InterruptedException {
         long wait = deadline.getTime() - System.currentTimeMillis();
         if (wait <= 0L) {
            return false;
         }
         return await(wait, TimeUnit.MILLISECONDS);
      }

      public void signal() {
      }

      public void signalAll() {
      }
   }
}
