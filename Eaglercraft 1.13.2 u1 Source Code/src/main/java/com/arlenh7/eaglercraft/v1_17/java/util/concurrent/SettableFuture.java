package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class SettableFuture<V> implements ListenableFuture<V> {
   private final List<Runnable> listeners = new ArrayList<>();
   private V value;
   private Throwable error;
   private boolean done;
   private boolean cancelled;

   public static <V> SettableFuture<V> create() {
      return new SettableFuture<>();
   }

   public static <V> SettableFuture<V> completedFuture(V value) {
      SettableFuture<V> future = new SettableFuture<>();
      future.complete(value);
      return future;
   }

   public static <V> SettableFuture<V> failedFuture(Throwable throwable) {
      SettableFuture<V> future = new SettableFuture<>();
      future.completeExceptionally(throwable);
      return future;
   }

   public boolean complete(V value) {
      return finish(value, null, false);
   }

   public boolean completeExceptionally(Throwable throwable) {
      return finish(null, throwable, false);
   }

   private boolean finish(V value, Throwable error, boolean cancelled) {
      List<Runnable> pending;
      synchronized (this) {
         if (this.done) {
            return false;
         }
         this.value = value;
         this.error = error;
         this.cancelled = cancelled;
         this.done = true;
         pending = new ArrayList<>(this.listeners);
         this.listeners.clear();
         this.notifyAll();
      }
      for (Runnable runnable : pending) {
         runnable.run();
      }
      return true;
   }

   @Override
   public void addListener(Runnable listener, Executor executor) {
      Runnable wrapped = () -> executor.execute(listener);
      boolean runNow;
      synchronized (this) {
         runNow = this.done;
         if (!runNow) {
            this.listeners.add(wrapped);
         }
      }
      if (runNow) {
         wrapped.run();
      }
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      return finish(null, new CancellationException(), true);
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public synchronized boolean isDone() {
      return this.done;
   }

   @Override
   public synchronized V get() throws InterruptedException, ExecutionException {
      while (!this.done) {
         this.wait();
      }
      if (this.error != null) {
         if (this.error instanceof CancellationException) {
            throw (CancellationException)this.error;
         }
         throw new ExecutionException(this.error);
      }
      return this.value;
   }

   @Override
   public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      long end = System.currentTimeMillis() + unit.toMillis(timeout);
      synchronized (this) {
         while (!this.done) {
            long wait = end - System.currentTimeMillis();
            if (wait <= 0L) {
               throw new TimeoutException();
            }
            this.wait(wait);
         }
      }
      return get();
   }
}
