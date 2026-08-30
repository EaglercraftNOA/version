package com.arlenh7.eaglercraft.v1_17.java.util.concurrent.locks;

public class ReentrantReadWriteLock implements ReadWriteLock {
   private final ReentrantLock sharedLock;
   private final ReadLock readLock;
   private final WriteLock writeLock;

   public ReentrantReadWriteLock() {
      this(false);
   }

   public ReentrantReadWriteLock(boolean fair) {
      this.sharedLock = new ReentrantLock(fair);
      this.readLock = new ReadLock(this);
      this.writeLock = new WriteLock(this);
   }

   @Override
   public ReadLock readLock() {
      return this.readLock;
   }

   @Override
   public WriteLock writeLock() {
      return this.writeLock;
   }

   public boolean isWriteLockedByCurrentThread() {
      return this.writeLock.isHeldByCurrentThread();
   }

   public int getReadHoldCount() {
      return this.readLock.getHoldCount();
   }

   public static class ReadLock extends ReentrantLock {
      private final ReentrantLock delegate;

      protected ReadLock(ReentrantReadWriteLock lock) {
         this.delegate = lock.sharedLock;
      }

      @Override
      public void lock() {
         this.delegate.lock();
      }

      @Override
      public void lockInterruptibly() throws InterruptedException {
         this.delegate.lockInterruptibly();
      }

      @Override
      public boolean tryLock() {
         return this.delegate.tryLock();
      }

      @Override
      public boolean tryLock(long time, java.util.concurrent.TimeUnit unit) throws InterruptedException {
         return this.delegate.tryLock(time, unit);
      }

      @Override
      public void unlock() {
         this.delegate.unlock();
      }

      @Override
      public Object newCondition() {
         throw new UnsupportedOperationException("Read locks do not support conditions");
      }

      @Override
      public boolean isHeldByCurrentThread() {
         return this.delegate.isHeldByCurrentThread();
      }

      @Override
      public int getHoldCount() {
         return this.delegate.getHoldCount();
      }
   }

   public static class WriteLock extends ReentrantLock {
      private final ReentrantLock delegate;

      protected WriteLock(ReentrantReadWriteLock lock) {
         this.delegate = lock.sharedLock;
      }

      @Override
      public void lock() {
         this.delegate.lock();
      }

      @Override
      public void lockInterruptibly() throws InterruptedException {
         this.delegate.lockInterruptibly();
      }

      @Override
      public boolean tryLock() {
         return this.delegate.tryLock();
      }

      @Override
      public boolean tryLock(long time, java.util.concurrent.TimeUnit unit) throws InterruptedException {
         return this.delegate.tryLock(time, unit);
      }

      @Override
      public void unlock() {
         this.delegate.unlock();
      }

      @Override
      public Object newCondition() {
         return this.delegate.newCondition();
      }

      @Override
      public boolean isHeldByCurrentThread() {
         return this.delegate.isHeldByCurrentThread();
      }
   }
}
