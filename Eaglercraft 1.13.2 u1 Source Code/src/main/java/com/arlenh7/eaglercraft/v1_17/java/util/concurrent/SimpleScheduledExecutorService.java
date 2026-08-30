package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SimpleScheduledExecutorService {
   private final String name;
   private int threadCounter;

   public SimpleScheduledExecutorService(String name) {
      this.name = name;
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      ScheduledTask task = new ScheduledTask(command, unit.toMillis(initialDelay), unit.toMillis(period), true);
      task.start(this.nextThreadName());
      return task;
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      ScheduledTask task = new ScheduledTask(command, unit.toMillis(initialDelay), unit.toMillis(delay), false);
      task.start(this.nextThreadName());
      return task;
   }

   private synchronized String nextThreadName() {
      return this.name + "-" + ++this.threadCounter;
   }

   private static final class ScheduledTask implements ScheduledFuture<Object>, Runnable {
      private final Runnable command;
      private final long initialDelayMillis;
      private final long intervalMillis;
      private final boolean fixedRate;
      private volatile boolean cancelled;
      private volatile boolean done;
      private volatile Throwable failure;
      private Thread thread;

      private ScheduledTask(Runnable command, long initialDelayMillis, long intervalMillis, boolean fixedRate) {
         this.command = command;
         this.initialDelayMillis = Math.max(0L, initialDelayMillis);
         this.intervalMillis = Math.max(1L, intervalMillis);
         this.fixedRate = fixedRate;
      }

      void start(String name) {
         this.thread = new Thread(this, name);
         this.thread.setDaemon(true);
         this.thread.start();
      }

      @Override
      public void run() {
         try {
            sleepInterruptibly(this.initialDelayMillis);
            long nextRun = System.currentTimeMillis();
            while (!this.cancelled) {
               this.command.run();
               if (this.fixedRate) {
                  nextRun += this.intervalMillis;
                  sleepInterruptibly(Math.max(0L, nextRun - System.currentTimeMillis()));
               } else {
                  sleepInterruptibly(this.intervalMillis);
               }
            }
         } catch (InterruptedException interruptedexception) {
            if (!this.cancelled) {
               Thread.currentThread().interrupt();
            }
         } catch (Throwable throwable) {
            this.failure = throwable;
         } finally {
            this.done = true;
            synchronized (this) {
               this.notifyAll();
            }
         }
      }

      private static void sleepInterruptibly(long millis) throws InterruptedException {
         if (millis > 0L) {
            Thread.sleep(millis);
         }
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         if (this.done) {
            return false;
         }
         this.cancelled = true;
         if (mayInterruptIfRunning && this.thread != null) {
            this.thread.interrupt();
         }
         synchronized (this) {
            this.notifyAll();
         }
         return true;
      }

      @Override
      public boolean isCancelled() {
         return this.cancelled;
      }

      @Override
      public boolean isDone() {
         return this.done;
      }

      @Override
      public synchronized Object get() throws InterruptedException, ExecutionException {
         while (!this.done) {
            this.wait();
         }
         if (this.cancelled) {
            throw new CancellationException();
         }
         if (this.failure != null) {
            throw new ExecutionException(this.failure);
         }
         return null;
      }

      @Override
      public synchronized Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
         long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
         while (!this.done) {
            long wait = deadline - System.currentTimeMillis();
            if (wait <= 0L) {
               throw new TimeoutException();
            }
            this.wait(wait);
         }
         return get();
      }

      @Override
      public long getDelay(TimeUnit unit) {
         return 0L;
      }
   }
}
