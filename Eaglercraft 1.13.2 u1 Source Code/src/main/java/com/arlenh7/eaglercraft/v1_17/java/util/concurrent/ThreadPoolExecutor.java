package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutor implements Executor {
   protected final int corePoolSize;
   protected final int maximumPoolSize;
   protected final long keepAliveTime;
   protected final TimeUnit keepAliveUnit;
   protected final BlockingQueue<Runnable> workQueue;
   protected final ThreadFactory threadFactory;
   private volatile boolean shutdown;

   public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                             BlockingQueue<Runnable> workQueue) {
      this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, null);
   }

   public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                             BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
      if (corePoolSize < 0 || maximumPoolSize <= 0 || keepAliveTime < 0 || maximumPoolSize < corePoolSize) {
         throw new IllegalArgumentException();
      }
      this.corePoolSize = corePoolSize;
      this.maximumPoolSize = maximumPoolSize;
      this.keepAliveTime = keepAliveTime;
      this.keepAliveUnit = unit;
      this.workQueue = workQueue;
      this.threadFactory = threadFactory;
   }

   @Override
   public void execute(Runnable command) {
      if (command == null) {
         throw new NullPointerException();
      }
      if (this.shutdown) {
         throw new IllegalStateException("Executor has been shut down");
      }
      command.run();
   }

   public Future<?> submit(Runnable task) {
      return submit(task, null);
   }

   public <T> Future<T> submit(Runnable task, T result) {
      if (task == null) {
         throw new NullPointerException();
      }
      if (this.shutdown) {
         throw new IllegalStateException("Executor has been shut down");
      }
      ImmediateFutureTask<T> future = new ImmediateFutureTask<>();
      try {
         task.run();
         future.complete(result);
      } catch (Throwable throwable) {
         future.fail(throwable);
      }
      return future;
   }

   public <T> Future<T> submit(Callable<T> task) {
      if (task == null) {
         throw new NullPointerException();
      }
      if (this.shutdown) {
         throw new IllegalStateException("Executor has been shut down");
      }
      ImmediateFutureTask<T> future = new ImmediateFutureTask<>();
      try {
         future.complete(task.call());
      } catch (Throwable throwable) {
         future.fail(throwable);
      }
      return future;
   }

   public void shutdown() {
      this.shutdown = true;
   }

   public java.util.List<Runnable> shutdownNow() {
      this.shutdown = true;
      return java.util.Collections.emptyList();
   }

   public boolean isShutdown() {
      return this.shutdown;
   }

   public boolean isTerminated() {
      return this.shutdown;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.shutdown;
   }

   public int getCorePoolSize() {
      return this.corePoolSize;
   }

   public int getMaximumPoolSize() {
      return this.maximumPoolSize;
   }

   private static final class ImmediateFutureTask<V> implements Future<V> {
      private V value;
      private Throwable error;
      private boolean done;

      void complete(V value) {
         this.value = value;
         this.done = true;
      }

      void fail(Throwable error) {
         this.error = error;
         this.done = true;
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         return false;
      }

      @Override
      public boolean isCancelled() {
         return false;
      }

      @Override
      public boolean isDone() {
         return this.done;
      }

      @Override
      public V get() throws ExecutionException {
         if (this.error != null) {
            if (this.error instanceof CancellationException) {
               throw (CancellationException) this.error;
            }
            throw new ExecutionException(this.error);
         }
         return this.value;
      }

      @Override
      public V get(long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
         return get();
      }
   }
}
