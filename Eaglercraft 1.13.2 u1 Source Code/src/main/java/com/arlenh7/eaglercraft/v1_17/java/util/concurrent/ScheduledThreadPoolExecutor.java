package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor {

   public ScheduledThreadPoolExecutor(int corePoolSize) {
      this(corePoolSize, null);
   }

   public ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
      super(corePoolSize, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
   }

   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      if (command == null || unit == null) {
         throw new NullPointerException();
      }
      if (isShutdown()) {
         throw new IllegalStateException("Executor has been shut down");
      }
      DelayedFutureTask<Void> task = new DelayedFutureTask<>(unit.toNanos(delay));
      try {
         command.run();
         task.complete(null);
      } catch (Throwable throwable) {
         task.fail(throwable);
      }
      return task;
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      if (callable == null || unit == null) {
         throw new NullPointerException();
      }
      if (isShutdown()) {
         throw new IllegalStateException("Executor has been shut down");
      }
      DelayedFutureTask<V> task = new DelayedFutureTask<>(unit.toNanos(delay));
      try {
         task.complete(callable.call());
      } catch (Throwable throwable) {
         task.fail(throwable);
      }
      return task;
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      return schedule(command, initialDelay, unit);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      return schedule(command, initialDelay, unit);
   }

   public void setRemoveOnCancelPolicy(boolean value) {
   }

   public boolean getRemoveOnCancelPolicy() {
      return false;
   }

   private static final class DelayedFutureTask<V> implements ScheduledFuture<V> {
      private final long delayNanos;
      private V value;
      private Throwable error;
      private boolean done;

      DelayedFutureTask(long delayNanos) {
         this.delayNanos = delayNanos;
      }

      void complete(V value) {
         this.value = value;
         this.done = true;
      }

      void fail(Throwable error) {
         this.error = error;
         this.done = true;
      }

      @Override
      public long getDelay(TimeUnit unit) {
         return unit.convert(this.delayNanos, TimeUnit.NANOSECONDS);
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
      public V get() throws java.util.concurrent.ExecutionException {
         if (this.error != null) {
            throw new java.util.concurrent.ExecutionException(this.error);
         }
         return this.value;
      }

      @Override
      public V get(long timeout, TimeUnit unit) throws java.util.concurrent.ExecutionException {
         return get();
      }
   }
}
